# Simple Market
Simple market is a mod that provides a simulated retail of food! 

The primary aim is to compel players to pursue multiple sources of food in order to create an impressive and varied market. 
In return, this mod provides an alternative acquisition path for tedious or nonrenewable resources (like mob drops or ores) via importing. 

Watch your market come to life as your impressive displays attract merchants and transform the area into a bustling hub of commerce.


Market display blocks will slowly retail their goods in exchange for coins! These coins can be converted into emeralds for vanilla-style trading or used to import goods into the market.

* Currently only planned to support food items but could probably be expanded with minimal effort to incorporate more goods

![Screenshot 2025-04-23 001203.png](Screenshot%202025-04-23%20001203.png)

##Values to adjust for release
- How quickly villagers despawn at night
- How quickly villagers try to eat from stalls
- How quickly villagers,cats, etc spawn from stalls
- How many villagers, cats, etc spawn from stalls

## prebeta TODO
- config files
- custom sprites for new mobs (villager done)
- zero out progress when item input is swapped
- Restrict market crate to only take food items
- change market crate to process food into coins
- ^^ starting to think about .json config/definitions for what is a food item and its worth
- change market crate to continue to process until stack capacity is reached
- multiple crates with same food item = locked processing, singleton - PersistentState
- improve model + texture. dyeable base mat for crate
- more display types. angled crates, hooks, frames, stands
- recipes
- odometer style "total sales" tracker - PersistentState

* coin interchange 
* importer + importer cards

## Future
- shipment crates. long processing time, coin bonus, require create.
- consider data attachment (new api) or mixin to track all villagers spawned by mod