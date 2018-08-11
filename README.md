# EmojiReactionView
[![platform](https://img.shields.io/badge/Platform-Android-yellow.svg?style=flat-square)](https://www.android.com)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat-square)](https://android-arsenal.com/api?level=16s)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](https://opensource.org/licenses/MIT)
[ ![Download](https://api.bintray.com/packages/dev-ritik/EmojiReactionView/EmojiReactionView/images/download.svg) ](https://bintray.com/dev-ritik/EmojiReactionView/EmojiReactionView/_latestVersion)

<img src="/sample/coverImage.png" align="centre">

<div align="centre" >An Android library to make Emoji Reactions on imageviews in a manner Instagram does this!.</div>

# Features
- <b>Design</b> : The library tries to be reasonably close to the original Instagram's design.
- <b>Customization</b> : Users can customize the look to a great extent easily and reliably.
- <b>Support for RecyclerView</b> : It can easily be used with Recycler views.
- <b>Optimum performance</b> : Efforts have been made to keep memory usage as small as possible.
- <b>Unharmed image</b> : The library doesn't change/modify the image, so every modification of image as imageview is possible

## Example
Below is the two sample for usage of the library:


<img src="/sample/simple.gif" align="left" width="400" >
<img src="/sample/recycler.gif" align="right" width="400">


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
    implementation 'com.ritik:emojireactionlibrary:1.0.0'
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

_With this all done and working, you have made to the default design!_

# Further customization
The library provides these attributes to modify default design to a custom one:

|Attribute            |Description                                                   | Type(Range)                                       |Default Value                        |
|---------------------|--------------------------------------------------------------|---------------------------------------------------|-------------------------------------|
|emojis               | Set the Emojis to be displayed in the semi-circular animation| reference                                         |                                     |
|set_emoji            | Set the current selected emoji                               | integer(>-1)(< noe*) (0 based)                    | -1                                  |
|home_Center_X       | Set the x coordinate of homeEmoji's center                  | dimensions                                        | 30 * density                        |
|home_Center_Y       | Set the y coordinate of homeEmoji's center                  | dimensions                                        | Height - 30 * density               |
|home_side           | Set the side length of homeEmoji                            | dimensions                                        | 50 * density                        |
|panel_center_X      | Set the x coordinate of the center of semi-circular animation| `dimension` or `fraction` [0%,100%] (w.r.t width)#| width / 2                           |
|panel_center_Y      | Set the y coordinate of the center of semi-circular animation| `dimension` or `fraction` [0%,100%] (w.r.t height)| Height - emojiSide / 2              |
|panel_radius        | Set the radius of semi-circular animation                    | dimension                                         | min(Height,Width) / 2 - 20 * density|
|panel_emoji_side     | Set the side of emojis on the panel                    | dimension                                         | 50 * density                        |
|emojis_rising_height | Set the height of the rising emojis(to start disappearing)   | `fraction` [0%,100%] (w.r.t height)#              | Height / 2                          |
|emojis_rising_speed  | Set the speed per frame of the rising emojis                 | dimension                                         | 10 * density                        |
|emojis_rising_number | Set the number of emojis rising in rising emojis animation   | integer                                           | 24                                  |

*noe = number of emojis.<br>
*#* measurement from bottom
# Public methods 

|Method                   |Description                                    |Data Type                   |
|-------------------------|-----------------------------------------------|----------------------------|
|getCentre                | Get the center of semi-circular animation     | int(Pixels)                |
|getRadius                | Get the radius of semi-circular animation     | int(Pixels)                |
|get/SetClickedEmojiNumber| Get/Set the current selected emoji            | int (-1 for none)(0 based) |
|getNumberOfEmojis        | Get the number Of emojis in the panel   | int                        |
|getEmojisRisingSpeed     | Get the speed per frame of the rising emojis  | int(Pixels)                |
|getHomeRect             | Get the Rect of the home emoji               | Rect                       |
|getPanelEmojiSide        | Get the side of emojis on the panel     | int(Pixels)                |
|get/SetHomeBitmap       | Get/Set the bitmap of the home emoji         | Bitmap                     |
|isHomeEmojiVisible      | Is home emoji visible                        | boolean                    |
|isPanelAnimWorking      | Is panel visible                        | boolean                    |
|isClickingAnimWorking    | Is the clicking animation working             | boolean                    |
|isEmojiRising            | Is emoji rising animation visible             | boolean                    |
|setHomeEmojiVisible     | Switch to home bitmap visible mode           | boolean                    |
|setPanelAnimWorking     | Start the circular animation                  | boolean                    |

# Contributions!

All contributions are welcome and appreciated. Please make a Pull Request or open an issue, if necessary.
This may also include any form of feature enhancement. Every constructive criticism is welcome.

# License
RotatingText is licensed under `MIT license`. View [license](LICENSE).

