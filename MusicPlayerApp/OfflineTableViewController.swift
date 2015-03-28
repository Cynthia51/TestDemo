//
//  OfflineTableViewController.swift
//  MusicPlayerApp
//
//  Created by Cynthia on 14-10-1.
//  Copyright (c) 2014年 Cynthia. All rights reserved.
//

import UIKit

class OfflineTableViewController: UITableViewController {

    var songList: Array<Song> = []
    var currentSong: Song!
    var index: Int!
    override func viewDidLoad() {
        super.viewDidLoad()

        readSongList()
    }
    
    func readSongList() {
        var path = NSHomeDirectory() + "/Documents"
        println(path)
        var file: Array = NSFileManager().contentsOfDirectoryAtPath(path, error: nil) as Array!
        
        for index in 1..<file.count{
        var song: Song = NSKeyedUnarchiver.unarchiveObjectWithFile(path + "/\(file[index])") as Song
        //println("\(song.songName)")
            self.songList.append(song)
        }
       
        //println("\(songList)")
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {

        return 1
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.songList.count
    }

    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        let cell: UITableViewCell = tableView.dequeueReusableCellWithIdentifier("offlineCell", forIndexPath: indexPath) as UITableViewCell
        currentSong = self.songList[indexPath.row]
        cell.textLabel?.text = currentSong.songName

        return cell
    }


    override func tableView(tableView: UITableView, willSelectRowAtIndexPath indexPath: NSIndexPath) -> NSIndexPath {
        currentSong = self.songList[indexPath.row]
        self.index = indexPath.row
        return indexPath
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject!) {
        var destViewController: OfflineViewController = segue.destinationViewController as OfflineViewController
        destViewController.song = currentSong
        destViewController.songList = self.songList
        destViewController.currentIndex = self.index
//        if destViewController.player.playing {
//            destViewController.playSongWithIndex(destViewController.currentIndex)
//        }
 
    }
}
