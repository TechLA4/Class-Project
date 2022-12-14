# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html

# useful for handling different item types with a single interface

from itemadapter import ItemAdapter

import pymysql

import pyecharts.options as opts
from pyecharts.charts import WordCloud


class TitanicPipeline:
    def process_item(self, item, spider):
        return item
