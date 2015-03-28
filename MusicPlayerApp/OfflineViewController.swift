//
//  OfflineViewController.swift
//  MusicPlayerApp
//
//  Created by Cynthia on 14-9-25.
//  Copyright (c) 2014年 Cynthia. All rights reserved.
//

import UIKit
import AVFoundation

class OfflineViewController: UIViewController,AVAudioPlayerDelegate {

    @IBOutlet weak var songNameLabel: UILabel!
    @IBOutlet weak var albumNameLabel: UILabel!
    @IBOutlet weak var artistNameLabel: UILabel!
    @IBOutlet weak var currentTimeLabel: UILabel!
    @IBOutlet weak var durationLabel: UILabel!
    @IBOutlet weak var timeSlider: UISlider!
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var playButton: UIButton!
    var song: Song!
    var songList: Array<Song>!
    var clickedPlayCount: Int = 0
    var currentIndex: Int!
    
    
    var timer: NSTimer!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        playSong()
    }
    
    func showInfo() {
        songNameLabel.text = song.songName
        albumNameLabel.text = song.albumName
        artistNameLabel.text = song.artistName
        
        var imagePath = NSHomeDirectory() + "/Documents/\(song.songName).jpg"
        imageView.image = UIImage(contentsOfFile: imagePath)
        
    }
    
    func audioPlayerDidFinishPlaying(player: AVAudioPlayer!, successfully flag: Bool) {
        timer.invalidate()
        var index = self.currentIndex + 1
        self.playSongWithIndex(index)
    }
    
    func playSong() {
        showInfo()
        var mp3Path = NSHomeDirectory() + "/Documents/\(song.songName).mp3"
        //println("\(mp3Path)")
        var url = NSURL(fileURLWithPath: mp3Path)
        MyPlayer.player = AVAudioPlayer(contentsOfURL: url, error: nil)
        MyPlayer.player.prepareToPlay()

        MyPlayer.player.play()
        
        MyPlayer.player.delegate = self
        
        playButton.setImage(UIImage(named: "05.jpg"), forState: UIControlState.Normal)
        
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
        self.currentTimeLabel.text = showTime(MyPlayer.player.currentTime)
        //歌曲总时间长度
        self.durationLabel.text = showTime(MyPlayer.player.duration)
    }

    @IBAction func didValueChange(sender: UISlider) {
        MyPlayer.player.currentTime = NSTimeInterval(sender.value) * MyPlayer.player.duration
    }
    
    @IBAction func cilckedPlay(sender: UIButton) {
        clickedPlayCount++
        playOrSuspend()
    }
    
    func playOrSuspend() {
        if clickedPlayCount % 2 == 0 {
            MyPlayer.player.play()
            playButton.setImage(UIImage(named: "05.jpg"), forState: UIControlState.Normal)
        }
        else {
            MyPlayer.player.stop()
            playButton.setImage(UIImage(named: "03.jpg"), forState: UIControlState.Normal)
        }
    }
    
    func playSongWithIndex(index: Int) {
        
        if index < self.songList.count && index >= 0{
            self.currentIndex = index
            self.song = self.songList[index]
            self.playSong()
            
        }
    }

    
    @IBAction func clickedPreSong(sender: UIButton) {
        var index = self.currentIndex - 1
        playSongWithIndex(index)
    }
    
    @IBAction func clickedNextSong(sender: UIButton) {
        var index = self.currentIndex + 1
        playSongWithIndex(index)
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

}
