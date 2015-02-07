# Snapshotting module for Play 2.3.x

Fork of the original https://github.com/vznet/play-snapshot implemented in Java. 

## Motivation

Adds an ability to distinguish snapshotting requests and return something different.
This is motivated by the requirements of Yandex https://help.yandex.com/webmaster/robot-workings/ajax-indexing.xml

> The main page’s HTML version is available at the address with the parameter "?_escaped_fragment_=" added to it. For   example: http://www.example.ru/?_escaped_fragment_=. Note: the value of the parameter should be empty.

> Include the mega tag `<meta name="fragment" content="!">` in the page’s code in order to alert the robot of the main page’s HTML version.

> You can use this meta tag on any AJAX page. For example, if the page located at http://www.example.ru/blog contains the meta tag <meta name="fragment" content="!">, the robot will index the page’s HTML version at the address http://www.example.ru/blog?_escaped_fragment_= .
>
> Note. 
>
> You should not place the meta tag in the HTML version of the document. If you do so, the page will not be indexed.

## Other changes 

* `@Snapshot` currently does not support options of the original implementation
* HtmlUnit is upgraded to 2.15

## Usage

Add resolver and dependencies to `build.sbt` of your project
```sbt
resolvers += "Snapshots" at "https://raw.github.com/stys/maven-releases/master/"

libraryDependencies ++= Seq(
    "com.stys" %% "platform-snapshot" % "1.1.0"    
)
```

Set some parameters in `application.conf`

```text
platform.snapshot.browserVersion = CHROME
platform.snapshot.waitForJavascriptMs = 2000
}
```

See HtmlUnit documentation for available browser versions.

Use `@Snapshot` to annotate actions
```java
import com.stys.platform.snapshot.Snapshot;

@Snapshot
public static Result index() {
	return ok(index.render());
}
```
