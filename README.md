# Snapshotting module for Play 2.3.x

Fork of the original https://github.com/vznet/play-snapshot implemented in Java. 

Adds an ability to distinguish snapshotting requests from other requests and return something different.
This is motivated by the requirements of Yandex https://help.yandex.com/webmaster/robot-workings/ajax-indexing.xml

> You can use this meta tag on any AJAX page. For example, if the page located at http://www.example.ru/blog contains the meta tag <meta name="fragment" content="!">, the robot will index the page’s HTML version at the address http://www.example.ru/blog?_escaped_fragment_= .
>
> Note. 
>
> You should not place the meta tag in the HTML version of the document. If you do so, the page will not be indexed.

# 