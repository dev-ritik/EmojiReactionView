# EmojiReactionView
[![platform](https://img.shields.io/badge/Platform-Android-yellow.svg?style=flat-square)](https://www.android.com)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat-square)](https://android-arsenal.com/api?level=16s)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](https://opensource.org/licenses/MIT)

An Android library to make Emoji Reactions on imageviews in a manner Instagram does this!.


# Features
- <b>Design</b> : The library tries to be reasonably close to the original Instagram's design.
- <b>Customization</b> : Users can customize the look to a great extent easily and reliably.
- <b>Support for RecyclerView</b> : It can easily be used with Recycler views.
- <b>Optimum performance</b> : Efforts have been made to keep memory usage as small as possible.
- <b>Unharmed image</b> : The library doesn't change/modify the image, so every modification of image as imageview is possible

# Usage
Just add the following dependency in your app's `build.gradle`

## Example
Add the following code in your xml file
```xml
<com.ritik.emojireactionlibrary.EmojiReactionView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:src="@drawable/image"
        app:emojis="@array/emoji" />
```
where `emoji` is an array resource which is the address to all emojis to be in the semi-circle like:
```xml

    <array name="emoji">
        <item>@drawable/em1</item>
        <item>@drawable/em2</item>
        <item>@drawable/em2</item>
    </array>
```

and for the cover emoji, rename that to **cover**, so that its resource id becomes `R.drawable.cover`
to override default cover emoji originally set.

_With this all done and working, you have made to the default design!_

# Further customization
The library provides these attributes to modify default design to a custom one:

|Attribute            |Description                                                     |Default Value                        |
|---------------------|----------------------------------------------------------------|-------------------------------------|
|emojis               | Set the Emojis to be displayed in the semi-circle ani              |                                     |
|set_emoji            | Set the current selected emoji                                 | -1                                  |
|cover_Center_X       | Set the x coordinate of coverEmoji's center                    | 30 * density                        |
|cover_Center_Y       | Set the y coordinate of coverEmoji's center                    | Height - 30 * density               |
|cover_side           | Set the side length of coverEmoji                              | 50 * density                        |
|circle_center_X      | Set the x coordinate of the center of semi-circular animation  | width / 2                           |
|circle_center_Y      | Set the y coordinate of the center of semi-circular animation  | Height - emojiSide / 2              |
|circle_radius        | Set the radius of semi-circular animation                      | min(Height,Width) / 2 - 20 * density|
|emoji_react_side     | Set the side of emojis on the semi-circle                      | min(Height,Width) / 2 - 20 * density|
|emojis_rising_height | Set the height of the rising emojis(to start disappearing)     | Height / 2                          |
|emojis_rising_speed  | Set the speed per frame of the rising emojis                   | 10 * density                        |
|emojis_rising_number | Set the number of emojis rising in rising emojis animation     | 24                                  |

# Public methods 

|Method                   |Description                                    |Data Type          |
|-------------------------|-----------------------------------------------|-------------------|
|getCentre                | Get the center of semi-circular animation     | int(Pixels)       |
|getRadius                | Get the radius of semi-circular animation     | int(Pixels)       |
|get/SetClickedEmojiNumber| Get/Set the current selected emoji            | int (-1 for none) |
|getNumberOfEmojis        | Get the number Of emojis in the semi-circle   | int               |
|getEmojisRisingSpeed     | Get the speed per frame of the rising emojis  | int(Pixels)       |
|getCoverRect             | Get the Rect of the cover emoji               | Rect              |
|getEmojiReactSide        | Get the side of emojis on the semi-circle     | int(Pixels)       |
|get/SetCoverBitmap       | Get/Set the bitmap of the cover emoji         | Bitmap            |
|isCoverEmojiVisible      | Is cover emoji visible                        | boolean           |
|isCircleAnimWorking      | Is semi-circle visible                        | boolean           |
|isClickingAnimWorking    | Is the clicking animation working             | boolean           |
|isEmojiRising            | Is emoji rising animation visible             | boolean           |

# Contributions!

All contributions are welcome and appreciated. Please make a Pull Request or open an issue, if necessary.
This may also include any form of feature enhancement. Every constructive criticism is welcome.

# License
RotatingText is licensed under `MIT license`. View [license](LICENSE.md).

