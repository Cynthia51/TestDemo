//
//  OnlineViewController.swift
//  MusicPlayerApp
//
//  Created by Cynthia on 14-9-25.
//  Copyright (c) 2014年 Cynthia. All rights reserved.
//

import UIKit
import AVFoundation

class OnlineViewController: UIViewController,AVAudioPlayerDelegate {

    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var offOnButton: UIButton!
    @IBOutlet weak var artistName: UILabel!
    @IBOutlet weak var albumName: UILabel!
    @IBOutlet weak var songName: UILabel!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var durationLabel: UILabel!
    @IBOutlet weak var timeSlider: UISlider!
    
    var channel: Channel!
    var songList: Array<Song> = []
    var currentIndex: Int!
    var currentSong: Song!
    var playClickedCount: Int = 0
    var timer: NSTimer!
    
    //下载数据的方法
    func loadData(path: String, dataHanlder: (NSData) -> Void) {
        var url = NSURL(string: path)
        var request = NSURLRequest(URL: url!)
        var mainQuene = NSOperationQueue.mainQueue()
        
        //异步下载
        NSURLConnection.sendAsynchronousRequest(request, queue: mainQuene, completionHandler: {
            (response: NSURLResponse!, data: NSData!, error: NSError!) -> Void in
            var httpResponse = response as NSHTTPURLResponse
            if httpResponse.statusCode == 200 {
                dataHanlder(data)
            }
        })
    }
    
    //3.显示歌曲信息
    func showSongInfo() {
       // var strSec: NSString!
       // var strMin: NSString!
        self.songName.text = self.currentSong.songName
        self.artistName.text = self.currentSong.artistName
        self.albumName.text = self.currentSong.albumName

        var picPath = "\(self.currentSong.songPicBig)"
        downloadPic(picPath)
        
        //println("\(self.currentSong.songPicBig)")
       
    }
    
    //下载图片
    func downloadPic(path: String) {
        loadData(path, dataHanlder: {
            (data: NSData) -> Void in
            self.imageView.image = UIImage(data: data)
        })
    }
    
    //5.播放歌曲
    func playSong(data: NSData){
    
        MyPlayer.player = AVAudioPlayer(data: data, error: nil)
        MyPlayer.player.prepareToPlay()
        MyPlayer.player.play()
        
        MyPlayer.player.delegate = self
        
        self.offOnButton.setImage(UIImage(named: "05.jpg"), forState: UIControlState.Normal)
        timer = NSTimer.scheduledTimerWithTimeInterval(0.01, target: self, selector: "refreshSlider", userInfo: nil, repeats: true)
        
    }
    
    //播放进度的时间显示
    func showTime(time: NSTimeInterval) -> NSString {
        var strSec: NSString!
        var strMin: NSString!
        var min = Int(time) / 60
        var sec = Int(time) % 60
        
        if min < 10 {
            strMin = "0" + "\(min)"
        }
        else {
            strMin = "\(min)"
        }
        
        if sec < 10 {
            strSec = "0" + "\(sec)"
        }
        else {
            strSec = "\(sec)"
        }
        return strMin + ":" + strSec
    }
    
    //刷新进度条
    func refreshSlider() {

        self.timeSlider.value = Float(MyPlayer.player.currentTime / MyPlayer.player.duration)
        //当前播放时间
        self.timeLabel.text = showTime(MyPlayer.player.currentTime)
        //歌曲总时间长度
        self.durationLabel.text = showTime(MyPlayer.player.duration)
    }
    
    //一曲播放完毕
    func audioPlayerDidFinishPlaying(player: AVAudioPlayer!, successfully flag: Bool) {
        timer.invalidate()
        
        var index = self.currentIndex + 1
        self.loadSongWithIndex(index)
        self.loadSongInfo(self.currentSong.id)
        
    }
    
    
    //4.加载歌曲
    func downloadSong(songPath: String) {
        loadData(songPath, dataHanlder: {
            (data: NSData) -> Void in
            self.playSong(data)
        })
    }
    
    //2.根据歌曲id获取歌曲信息
    func loadSongInfo(id: Int) {
        var path = "http://music.baidu.com/data/music/fmlink?type=mp3&rate=1&format=json&songIds=\(id)"
        loadData(path, dataHanlder: {
            (data: NSData) -> Void in
            var dict: NSDictionary = NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.AllowFragments, error: nil) as NSDictionary
            
            //查找需要到歌曲信息
            var dataDict: NSDictionary = dict["data"] as NSDictionary
            var list: NSArray = dataDict["songList"] as NSArray
            var songDict: NSDictionary = list[0] as NSDictionary
            
            //更新歌曲信息
            self.currentSong.refreshSong(songDict)
            
            //显示歌曲信息
            self.showSongInfo()
            
            //下载歌曲
            self.downloadSong(self.currentSong.songLink)
        })
        
}

    //1.加载歌曲信息
    func loadSongList() {
        var path = "http://fm.baidu.com/dev/api/?tn=playlist&special=flash&prepend=&format=json&_=1378945264366&id=" + String(channel.id)
        loadData(path, dataHanlder: {
            (data: NSData) -> Void in
            var dict: NSDictionary = NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.AllowFragments, error: nil) as NSDictionary
            
            var list: NSArray = dict["list"] as NSArray
            for d in list {
                var song = Song()
                song.id = d["id"] as Int
                self.songList.append(song)
            }
            if self.songList.count != 0 {
                
                //自动下载第一首歌
                self.currentSong = self.songList[0]
                
                self.currentIndex = 0
                self.loadSongInfo(self.currentSong.id)
            }
        })
    }
    

    override func viewDidLoad() {
        super.viewDidLoad()
        loadSongList()
    }

    //播放、暂停
    @IBAction func didPlayClicked(sender: UIButton) {
        playClickedCount++
        if playClickedCount % 2 == 0 {
            MyPlayer.player.play()
            self.offOnButton.setImage(UIImage(named: "05.jpg"), forState: UIControlState.Normal)
        }
        else {
            MyPlayer.player.stop()
            self.offOnButton.setImage(UIImage(named: "03.jpg"), forState: UIControlState.Normal)
        }
    }
    
    //根据index加载歌曲
    func loadSongWithIndex(index: Int) {
        
        if index < self.songList.count && index >= 0{
            self.currentIndex = index
            self.currentSong = self.songList[index]
            MyPlayer.player.stop()
            self.timeSlider.value = 0
            self.timeLabel.text = ""
        
            self.loadSongInfo(self.currentSong.id)
        }
    }
    
    //上一曲
    @IBAction func didPreClicked(sender: UIButton) {
        var index = self.currentIndex - 1
        loadSongWithIndex(index)
    }
    
    //下一曲
    @IBAction func didNextClicked(sender: UIButton) {
        var index = self.currentIndex + 1
        loadSongWithIndex(index)
    }
    
    //拖动进度条
    @IBAction func didValueChange(sender: UISlider) {
        MyPlayer.player.currentTime = NSTimeInterval(sender.value) * MyPlayer.player.duration
    }
    
    //下载歌曲
    @IBAction func loadSong(sender: UIBarButtonItem) {
        //保存歌曲到Song文件夹
        var sLink = self.currentSong.songLink
        loadData(sLink, dataHanlder: {
            (data: NSData) -> Void in
            println(NSHomeDirectory);
            var songPath = NSHomeDirectory() + "/Documents/" + "\(self.currentSong.songName)" + ".mp3"
            data.writeToFile(songPath, options: NSDataWritingOptions.AtomicWrite, error: nil)
        })
        
        //保存图片到Pic文件夹
        var picLink = self.currentSong.songPicBig
        loadData(picLink, dataHanlder: {
            (data: NSData) -> Void in
            var picPath = NSHomeDirectory() + "/Documents/" + "\(self.currentSong.songName)" + ".jpg"
            data.writeToFile(picPath, options: NSDataWritingOptions.AtomicWrite, error: nil)
        })

        //保存歌曲信息到Info文件夹
        var song = self.currentSong
        var infoPath = NSHomeDirectory() + "/Documents/" + "\(self.currentSong.songName)" + ".plist"
        println("\(infoPath)")
        NSKeyedArchiver.archiveRootObject(song, toFile: infoPath)
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

}
