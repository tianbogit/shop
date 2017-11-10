# -*- coding: utf-8 -*-

# Define here the models for your scraped items
#
# See documentation in:
# http://doc.scrapy.org/en/latest/topics/items.html

import scrapy

# 目的地产品
class DestItem(scrapy.Item):   
    Title = scrapy.Field()
    Remark = scrapy.Field()
    Place = scrapy.Field()
    Score = scrapy.Field()
    TripType = scrapy.Field()
    BasePrice = scrapy.Field()
    ImgUrl = scrapy.Field()

# 机票
class Ticket(scrapy.Item):
    LeaveCity = scrapy.Field()
    ArriveCity = scrapy.Field()
    AirLine = scrapy.Field()
    AirPlaneType = scrapy.Field()
    LeavePort = scrapy.Field()
    ArrivePort = scrapy.Field()
    LeaveDate = scrapy.Field()
    LeaveTime = scrapy.Field()
    ArriveDate = scrapy.Field()
    ArriveTime = scrapy.Field()
    Price = scrapy.Field()
    Duration = scrapy.Field()
