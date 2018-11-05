# <div align="center"> EmojiReactionView </div>

<div align="center">
	<a href="https://www.android.com">
    <img src="https://img.shields.io/badge/platform-Android-brightgreen.svg"
      alt="Platform" />
  </a>
	<a href="https://android-arsenal.com/api?level=19">
    <img src="https://img.shields.io/badge/API-19%2B-blue.svg"
      alt="API" />
  </a>
	<a href="https://opensource.org/licenses/MIT">
    <img src="https://img.shields.io/badge/License-MIT-red.svg"
      alt="License: MIT" />
  </a>
    <a href="https://bintray.com/dev-ritik/EmojiReactionView/EmojiReactionView/_latestVersion">
    <img src="https://api.bintray.com/packages/dev-ritik/EmojiReactionView/EmojiReactionView/images/download.svg"
      alt="Download" />
  </a>
  <a href="https://sputnik.ci/app#/builds/dev-ritik/EmojiReactionView">
    <img src="https://sputnik.ci/conf/badge"
      alt="Sputnik" />
  </a>
  <a href="https://travis-ci.com/dev-ritik/EmojiReactionView">
    <img src="https://travis-ci.com/dev-ritik/EmojiReactionView.svg?branch=master"
      alt="Build Status" />
  </a>
</div><br>

<img src="/sample/coverImage.png" align="centre">

<div align="center">An Android library to make Emoji Reactions on imageviews in a manner Instagram does this!.</div><br>

___
# Table of contents

  * [Features](#features)
  * [Example](#example)
  * [Usage](#usage)
  * [Further customization](#further-customization)
  * [Public methods](#public-methods)
  * [Contributions](#contributions)
  * [License](#license)

# Features
- <b>Design</b> : The library tries to be reasonably close to the original Instagram's design.
- <b>Customization</b> : Users can customize the look to a great extent easily and reliably.
- <b>Support for RecyclerView</b> : It can easily be used with Recycler views.
- <b>Optimum performance</b> : Efforts have been made to keep memory usage as small as possible.
- <b>Unharmed image</b> : The library doesn't change/modify the image, so every modification of image as imageview is possible

# Example
Below is the two sample for usage of the library:

<img src="/sample/simple.gif" align="right" width="418" >
<img src="/sample/recycler.gif" align="left" width="418">

<br>

# Usage
Just add the following dependency in your app's `build.gradle`
```groovy
repositories {
    maven {
        url 'https://dl.bintray.com/dev-ritik/EmojiReactionView/'
    }
}

dependencies {
    implementation 'com.ritik:emojireactionlibrary:1.0.2'
}

```

Add the following code in your xml file
```xml
<com.ritik.emojireactionlibrary.EmojiReactionView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:src="@drawable/image"
        app:emojis="@array/emoji" />
```
where `emoji` is an array resource which is the address to all emojis to be in the panel like:
```xml

    <array name="emoji">
        <item>@drawable/em1</item>
        <item>@drawable/em2</item>
        <item>@drawable/em2</item>
    </array>
```

and for the home emoji, rename that to **home**, so that its resource id becomes `R.drawable.home`
to override default home emoji originally set.

To get notified of when the emojis are clicked, you can use:
```java
myImage.setOnEmojiClickListener(new ClickInterface() {
    @Override
    public void onEmojiClicked(int emojiIndex, int x, int y) {
        // emojiIndex is the index of the emoji being clicked (0 based)
        // x,y are the coordinates of the clicked position relative to the image 
        // (if x && y == -1 => changed by program(SetClickedEmojiNumber)
    }

    @Override
    public void onEmojiUnclicked(int emojiIndex, int x, int y) {
        // emojiIndex is the index of the emoji being clicked (0 based)
        // x,y are the coordinates of the clicked position relative to the image 
        // (if x && y == -1 => changed by program(SetClickedEmojiNumber)
    }
});
```

**If you are struck somewhere, you can always check its usage in the sample app for [Simple](https://github.com/dev-ritik/EmojiReactionView/blob/master/app/src/main/java/com/ritik/emojireactionview/SimpleExample.java) 
and [RecyclerView](https://github.com/dev-ritik/EmojiReactionView/blob/master/app/src/main/java/com/ritik/emojireactionview/FeedAdapter.java) usage.**

_With this all done and working, you have made to the default design!_

# Further customization
The library provides these attributes to modify default design to a custom one:

|Attribute            |Description                                                 | Type(Range)                                       |Default Value                        |
|---------------------|------------------------------------------------------------|---------------------------------------------------|-------------------------------------|
|emojis               | Set the emojis to be displayed in the panel animation      | reference                                         |                                     |
|set_emoji            | Set the current selected emoji                             | integer(>-1)(< noe*) (0 based)                    | -1                                  |
|home_Center_X        | Set the x coordinate of homeEmoji's center                 | dimensions                                        | 30 * density                        |
|home_Center_Y        | Set the y coordinate of homeEmoji's center                 | dimensions                                        | Height - 30 * density               |
|home_side            | Set the side length of homeEmoji                           | dimensions                                        | 50 * density                        |
|panel_center_X       | Set the x coordinate of the center of panel animation      | `dimension` or `fraction` [0%,100%] (w.r.t width)#| width / 2                           |
|panel_center_Y       | Set the y coordinate of the center of panel animation      | `dimension` or `fraction` [0%,100%] (w.r.t height)| Height - emojiSide / 2              |
|panel_radius         | Set the radius of panel animation                          | dimension                                         | min(Height,Width) / 2 - 20 * density|
|panel_emoji_side     | Set the side of emojis on the panel                        | dimension                                         | 50 * density                        |
|emojis_rising_height | Set the height of the rising emojis(to start disappearing) | `fraction` [0%,100%] (w.r.t height)#              | Height / 2                          |
|emojis_rising_speed  | Set the speed per frame of the rising emojis               | dimension                                         | 10 * density                        |
|emojis_rising_number | Set the number of emojis rising in rising emojis animation | integer                                           | 24                                  |

*noe = number of emojis.<br>
*#* measurement from bottom
# Public methods 

|Method                   |Description                                   |Data Type                   |
|-------------------------|----------------------------------------------|----------------------------|
|getCentre                | Get the center of panel animation            | int(Pixels)                |
|getRadius                | Get the radius of panel animation            | int(Pixels)                |
|get/SetClickedEmojiNumber| Get/Set the current selected emoji           | int (-1 for none)(0 based) |
|getNumberOfEmojis        | Get the number Of emojis in the panel        | int                        |
|getEmojisRisingSpeed     | Get the speed per frame of the rising emojis | int(Pixels)                |
|getHomeRect              | Get the Rect of the home emoji               | Rect                       |
|getPanelEmojiSide        | Get the side of emojis on the panel          | int(Pixels)                |
|get/SetHomeBitmap        | Get/Set the bitmap of the home emoji         | Bitmap                     |
|isHomeEmojiVisible       | Is home emoji visible                        | boolean                    |
|isPanelAnimWorking       | Is panel visible                             | boolean                    |
|isClickingAnimWorking    | Is the clicking animation working            | boolean                    |
|isEmojiRising            | Is emoji rising animation visible            | boolean                    |
|setHomeEmojiVisible      | Switch to home bitmap visible mode           | boolean                    |
|setPanelAnimWorking      | Start the panel animation                    | boolean                    |

# Contributions

All contributions are welcome and appreciated. Please make a Pull Request or open an issue, if necessary.
This may also include any form of feature enhancement. Every constructive criticism is welcome.
See [Contributing.md](https://github.com/dev-ritik/EmojiReactionView/blob/master/CONTRIBUTING.md)

# License
This library is licensed under `MIT license`. View [license](LICENSE).
