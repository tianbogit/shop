# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: http://doc.scrapy.org/en/latest/topics/item-pipeline.html

import pymysql
from tripspider.items import DestItem
from tripspider.items import Ticket

def dbHandle():
    conn = pymysql.connect(
        host='******',
        user='****',
        passwd='*****',
        charset='utf8',
        use_unicode=False
    )
    return conn


class tripPipeline(object):
    def process_item(self, item, spider):
        dbObject = dbHandle()
        cursor = dbObject.cursor()
        if isinstance(item,DestItem):
            sql = 'insert into trip.products(Title,Remark,TripType,Amount,ImgUrl) values (%s,%s,%s,%s,%s)'
            try:
                cursor.execute(sql,
                               (item['Title'],item['Remark'], item['TripType'], item['BasePrice'], item['ImgUrl']))
                dbObject.commit()
            except Exception as e:
                print(e)
                dbObject.rollback()
            return item

        if isinstance(item,Ticket):
            sql = 'insert into trip.tickets(LeaveCity,ArriveCity,AirLine,AirPlaneType,LeavePort,ArrivePort,LeaveDate,LeaveTime,ArriveDate,ArriveTime,Price) values (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)'
            try:
                cursor.execute(sql,
                               (item['LeaveCity'],
                                item['ArriveCity'],
                                item['AirLine'],
                                item['AirPlaneType'],
                                item['LeavePort'],
                                item['ArrivePort'],
                                item['LeaveDate'],
                                item['LeaveTime'],
                                item['ArriveDate'],
                                item['ArriveTime'],
                                item['Price']
                               ))
                dbObject.commit()
            except Exception as e:
                print(e)
                dbObject.rollback()
            return item

        return item
