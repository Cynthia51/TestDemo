//
//  Channel.swift
//  MusicPlayerApp
//
//  Created by Cynthia on 14-9-25.
//  Copyright (c) 2014年 Cynthia. All rights reserved.
//

import UIKit

class Channel: NSObject {
 
    var id: NSString!
    var title: NSString!
    var cate_id: NSString!
    var cate: NSString!
    
    init(dict: NSDictionary) {
        super.init()
        self.id = dict["id"] as NSString
        self.title = dict["title"] as NSString
        self.cate_id = dict["cate_id"] as NSString
        self.cate = dict["cate"] as NSString
    }
}
