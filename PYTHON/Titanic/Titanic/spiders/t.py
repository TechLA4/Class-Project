import time

import scrapy
from Titanic.items import TitanicItem

class TitanicSpider(scrapy.Spider):
    name = 'Titanic'
    allowed_domains = ['movie.douban.com']
    start_urls = ['https://movie.douban.com/subject/1292722/comments']

    urls='https://movie.douban.com/subject/1292722/comments?start=20&limit=20'

    page=0
    start=0

    def parse(self, response):
        base=response.xpath("//div[@class='comment']")

        print('史可法尽快回复很多事佛教哈多喝水发放空间的爽肤水大剑砍')

        for i in base:

            comments=i.xpath("./p/span/text()").get()

            star=i.xpath(".//span[@class='votes vote-count']/text()").get()


            print(comments)
            print(star)


            item=TitanicItem(comments=comments,star=star)
            yield item

        time.sleep(2)


        if(self.page<=10):
            self.page=self.page+1
            self.start=self.start+20


            url_b='https://movie.douban.com/subject/1292722/comments?start='
            url=url_b+str(self.start)+'&limit=20'

            print(url)

            yield scrapy.Request(url=url,callback=self.parse)

        print('史可法尽快回复很多事佛教哈多喝水发放空间的爽肤水大剑砍')

#IP被封了

