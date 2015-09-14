#!/usr/bin/python
#-*- encoding:utf-8 -*-
import tornado.ioloop
import tornado.web
import shutil
import os
from codemanager import CodeManager;

cm = CodeManager();

class UploadFileHandler(tornado.web.RequestHandler):
    def get(self):
        self.write('''
<html>
  <head><title>Upload File</title></head>
  <body>
    <form action='file' enctype="multipart/form-data" method='post'>
    <input type='file' name='file'/><br/>
    <input type='submit' value='submit'/>
    </form>
  </body>
</html>
''')
 
    def post(self):
        print "UploadFileHandler POST"
        upload_path=os.path.join(os.path.dirname(__file__),'files')  #文件的暂存路径
        file_metas=self.request.files['file']    #提取表单中‘name’为‘file’的文件元数据
        for meta in file_metas:
            filename=meta['filename']
            filepath=os.path.join(upload_path,filename)
            print "A FILE:"+filename
            with open(filepath,'wb') as up:      #有些文件需要已二进制的形式存储，实际中可以更改
                up.write(meta['body'])
            self.write('finished!')


class CodeFileHandler(tornado.web.RequestHandler):
    def get(self):
        pass;

    def post(self, livecode):
        global cm;
        print "[CodeFileHandler]lc:"+livecode;
        if livecode is None:
            self.write("{'errorcode':'invalid code'}")
            return
        wc = cm.checkLiveCode(livecode);
        if(wc is None):
            #return 404
            #raise tornado.web.HTTPError(404)
           self.write("{'errorcode':'invalid code'}")
           return
        #os.pathsep
        upload_path=os.path.join(os.path.dirname(__file__),'files'+os.sep+livecode)  #文件的暂存路径
        if not os.path.isdir(upload_path):
            os.mkdir(upload_path)
        self.acceptPostFile(upload_path)

    def acceptPostFile(self, savepath):
        print "[CodeFileHandler]UploadFileHandler POST"
        file_metas=self.request.files['file']    #提取表单中‘name’为‘file’的文件元数据
        for meta in file_metas:
            filename=meta['filename']
            filepath=os.path.join(savepath,filename)
            print "[CodeFileHandler]A FILE:"+filename
            with open(filepath,'wb') as up:      #有些文件需要已二进制的形式存储，实际中可以更改
                up.write(meta['body'])
            self.write('finished!')


class CodeHandler(tornado.web.RequestHandler):
    def get(self):
        print "[CodeHandler]"
        global cm;
        ret = cm.createCode();
        self.write('{"lc":"%s", "wc":"%s"}'%(ret[0],ret[1]))
 
app=tornado.web.Application([
    (r'/file',UploadFileHandler),
    (r'/code',CodeHandler),
    (r"/codefile/(.*)", CodeFileHandler),
])

#(r"/codefile/(P?<livecode>.*)", CodeFileHandler),
 
if __name__ == '__main__':
    print "Listen on 3000"
    app.listen(3000)
    tornado.ioloop.IOLoop.instance().start()
