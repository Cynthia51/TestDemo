//
//  OnlineTableViewController.swift
//  MusicPlayerApp
//
//  Created by Cynthia on 14-9-25.
//  Copyright (c) 2014年 Cynthia. All rights reserved.
//

import UIKit

class OnlineTableViewController: UITableViewController {

    var channelList: Array<Channel> = []
    var currentChannel: Channel!
    func loadChannelList() {
        var url = NSURL(string: "https://gitcafe.com/wcrane/XXXYYY/raw/master/baidu.json")
        var request = NSURLRequest(URL: url!)
        var mainQuene = NSOperationQueue.mainQueue()
        NSURLConnection.sendAsynchronousRequest(request, queue: mainQuene, completionHandler: {
            (response: NSURLResponse!, data: NSData!, error: NSError!) -> Void in
            var httpResponse = response as NSHTTPURLResponse

            if httpResponse.statusCode == 200 {
                var array: NSArray = NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.AllowFragments, error: nil) as NSArray
                //println("\(array)")
                for element in array {
                    var channel = Channel(dict: element as NSDictionary)
                    self.channelList.append(channel)
                }
                self.tableView.reloadData()
                
            }
        })
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        loadChannelList()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // #pragma mark - Table view data source

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {

        return self.channelList.count
    }

    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        let cell: UITableViewCell = tableView.dequeueReusableCellWithIdentifier("cell", forIndexPath: indexPath) as UITableViewCell
        var currentChannel: Channel = self.channelList[indexPath.row]
        cell.textLabel?.text = currentChannel.title
        return cell
    }
   
    override func tableView(tableView: UITableView, willSelectRowAtIndexPath indexPath: NSIndexPath) -> NSIndexPath {
        currentChannel = self.channelList[indexPath.row]
        return indexPath
    }

    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject!) {
        var destViewController: OnlineViewController = segue.destinationViewController as OnlineViewController
        destViewController.channel = currentChannel
    }

}
