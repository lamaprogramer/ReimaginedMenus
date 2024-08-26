# Reimagined World Menu

Completely Re-designs the Create World menu, as well as making some changes to the world list.

## Features

- Complete menu overhaul

- Customizable image that represents each world type (default, flat, amplified, etc.)

- Ability to add a custom icon to your world upon creation.

- Increased resolution for world icons.

- Translations for:
  
      - Italian
  
      - Spanish
  
      - Japanese
  
      - Portuguese
  
      - Polish
  
      - Russian
  
      - German
  
      - Serbian

      - Bulgarian

      - Czech

      - Tatar

      - Chinese

## Resource packs

This mod allows you to customize two things with resource packs:

- World type image

- Tab icons

If you need exaples of what the pack should look like, then you can take a look in the source code's resource folder,
Everything here has been used in the mod's source.

### World preset images

To add custom world preset images, you must first create a resource pack, and in the folder `reimaginedmenus/textures/misc`,
you must have an image with the name of the world preset identifier, for example, 
an amplified world would need an image titled `amplified`.

### Tab Icons

To add custom world preset images, in your resource pack, and in the folder `reimaginedmenus/textures/misc`,
create a json file titled `tabicons.json`, and in that file, put:

```json
{
  "general_settings_icon": "modid:path/to/texture",
  "world_settings_icon": "modid:path/to/texture",
  "advanced_settings_icon": "modid:path/to/texture"
}
```

You can choose whatever texture you want, it can be vannila, modded, or a custom texture.
If you want a custom texture, then just the identifier should look like this: `reimaginedmenus:path/to/texture`, 
then you just provide the textures in your resource pack.


## Feedback

I'm entirely open to design changes and will consider adding your suggestion.
I'm also open to adding and/or editing translations for your language, you will have two options:

    - Translate the text yourself
    
    - Or have me use Google Translate (Which is prone to inaccuracy, but can be quicker)
