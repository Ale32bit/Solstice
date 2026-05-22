# Powertool

This module implements commands executable from using items.

## Commands

!!! command "powertool set &lt;action&gt; &lt;command&gt;"

    Bind a command to the item action.

    It is possible to use placeholders in the commands.

    **Actions**

    - `use`: On item use (right click) while not looking at a block nor at an entity.
    - `attack_block`: On digging a block.
    - `attack_entity`: On entity attack. The placeholder context will be bound to the entity.
    - `interact_block`: On use on block.
    - `interact_entity`: On use on entity. The placeholder context will be bound to the entity.

    **Permissions**

    * `solstice.powertool.base` - Default: 2

    **Aliases**

    * pt

!!! command "powertool clear [action]"

    Clear commands from the item.

    If action is missing, all actions will be cleared.
    
    **Permissions**

    * `solstice.powertool.base` - Default: 2

    **Aliases**

    * pt

!!! command "powertool check"

    Check commands bound to the item.
    
    **Permissions**

    * `solstice.powertool.base` - Default: 2

    **Aliases**

    * pt