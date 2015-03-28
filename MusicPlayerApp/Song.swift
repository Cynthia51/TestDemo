//
//  Song.swift
//  MusicPlayerApp
//
//  Created by Cynthia on 14-9-25.
//  Copyright (c) 2014年 Cynthia. All rights reserved.
//

import UIKit

class Song: NSObject, NSCoding{
    var id: Int!
    var songName: NSString!
    var artistName: NSString!
    var albumName: NSString!
    var songPicSmall: NSString!
    var songPicBig: NSString!
    var lrcLink: NSString!
    var songLink: NSString!
    var showLinK: NSString!
    
    override init() {
        
    }
    
    func encodeWithCoder(aCoder: NSCoder) {
        aCoder.encodeObject(self.id, forKey: "id")
        aCoder.encodeObject(self.songName, forKey: "songName")
        aCoder.encodeObject(self.artistName, forKey: "artistName")
        aCoder.encodeObject(self.albumName, forKey: "albumName")
        aCoder.encodeObject(self.songPicBig, forKey: "songPicBig")
        
    }
     required init(coder aDecoder: NSCoder) {
        self.id = aDecoder.decodeObjectForKey("id") as Int
        self.songName = aDecoder.decodeObjectForKey("songName") as NSString
        self.artistName = aDecoder.decodeObjectForKey("artistName") as NSString
        self.albumName = aDecoder.decodeObjectForKey("albumName") as NSString
        self.songPicBig = aDecoder.decodeObjectForKey("songPicBig") as NSString
    }
    func refreshSong(dict: NSDictionary) {
        self.songName = dict["songName"] as NSString
        self.artistName = dict["artistName"] as NSString
        self.albumName = dict["albumName"] as NSString
        self.songPicBig = dict["songPicBig"] as NSString
        self.songPicSmall = dict["songPicSmall"] as NSString
        self.lrcLink = dict["lrcLink"] as NSString
        self.songLink = dict["songLink"] as NSString
        self.showLinK = dict["showLink"] as NSString
    }
}
