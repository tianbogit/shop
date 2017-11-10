# -*- coding: utf-8 -*-
import scrapy
from selenium import webdriver
from tripspider.items import Ticket


class ticketSpider(scrapy.Spider):
    name = 'ticket'
    allowed_domains = ['flights.ctrip.com']
    start_urls = ['http://flights.ctrip.com/booking/SHA-TAO-day-1.html?DDate1=2017-12-20']

    def __init__(self):
        # 进入浏览器设置
        options = webdriver.ChromeOptions()
        # 设置中文
        options.add_argument('lang=zh_CN.UTF-8')
        # 更换头部
        options.add_argument(
            'user-agent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.89 Safari/537.36"')

        self.browser = webdriver.Chrome(chrome_options=options)

    def __del__(self):
        self.browser.close()

    def parse(self, response):
        self.browser.get(response.url)
        lis = self.browser.find_elements_by_xpath("//div[@class='search_box search_box_tag search_box_light ']")
        print(len(lis))
        for li in lis:
            item = Ticket()
            item['LeaveCity'] = "SZX"
            item['ArriveCity'] = "CGO"
            item['AirLine'] = li.find_element_by_xpath("table/tbody/tr/td[1]/div[1]/strong").text
            item['AirPlaneType'] = li.find_element_by_xpath('table/tbody/tr/td[1]/div[2]/span').text
            item['LeavePort'] = li.find_element_by_xpath('table/tbody/tr/td[2]/div[2]').text
            item['ArrivePort'] = li.find_element_by_xpath('table/tbody/tr/td[4]/div[2]').text
            item['LeaveDate'] = "2017-12-08"
            item['LeaveTime'] = li.find_element_by_xpath('table/tbody/tr/td[2]/div[1]/strong').text
            item['ArriveDate'] = "2017-12-08"
            item['ArriveTime'] = li.find_element_by_xpath('table/tbody/tr/td[4]/div[1]/strong').text
            price = li.find_element_by_xpath('table/tbody/tr/td[7]/span/span').text
            item['Price'] = price.replace("\xa5","")
            yield item








