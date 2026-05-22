# Developers

Would you like to interface with Solstice?

Add the Maven repository:

```groovy
maven {
    name "alexdevs"
    url "https://maven.alexdevs.me/releases"
}
```

...and add the dependency:

```groovy
// Solstice Essentials
modImplementation "me.alexdevs:solstice:VERSION"
```

Replace `VERSION` with the preferred version. eg. `1.5.0+1.20.1` (without `v`).

## Adding modules

This section explains how you can setup the integration of custom modules to Solstice.

Create a class that implements `me.alexdevs.solstice.api.module.ModuleEntrypoint`.

This interface exposes the method `register()`, used to return the list of modules to load by Solstice.

```java
package com.example.modules;

import me.alexdevs.solstice.api.module.ModuleBase;
import me.alexdevs.solstice.api.module.ModuleEntrypoint;

import java.util.HashSet;
import java.util.List;

public class ModuleProvider implements ModuleEntrypoint {

    // Add module instances here
    private static final List<? extends ModuleBase> modules = List.of(
            new MyModule(),
            new MyCoolModule(),
    );

    // Return a HashSet of the list of modules.
    // This method is only called once!
    @Override
    public HashSet<ModuleBase> register() {
        return new HashSet<>(modules);
    }
}
```
To make the class discoverable by Solstice, add the `"solstice"` entry point to your `fabric.mod.json`, under the `"entrypoints"` field, along the `"main"` entry point:

```json
"entrypoints": {
    // Main entrypoint of your Fabric mod
    "main": [
        "com.example.modules.MyMod"
    ],

    // Add this entry point to the list:
    "solstice": [
        "com.example.modules.ModuleProvider"
    ]
    // ...
},
```

### Module example

All Solstice modules extends the abstract class `me.alexdevs.solstice.api.module.ModuleBase`.

Modules need to provide a String ID to the super constructor, ideally all lowercase, this ID is used to differentiate between modules and is also used to make permission nodes.

```java
package com.example.modules.mymodule;

import me.alexdevs.solstice.Solstice;
import me.alexdevs.solstice.api.module.ModuleBase;

// Use ModuleBase.Toggleable to make it toggleable from modules.conf.
public class MyModule extends ModuleBase {
    public static final String ID = "mymodule";

    public MyModule() {
        super(ID);
    }

    @Override
    public void init() {
        // Register the configuration section
        Solstice.configManager.registerData(ID, MyModuleConfig.class, MyModuleConfig::new);

        // Register the locale
        Solstice.localeManager.registerModule(ID, MyModuleLocale.MODULE);

        // Register the player data
        Solstice.playerData.registerData(ID, MyModulePlayerData.class, MyModulePlayerData::new);

        // Register the server data
        Solstice.serverData.registerData(ID, MyModuleServerData.class, MyModuleServerData::new);

        // Add a command to your module
        commands.add(new MyCommand(this));
    }
}
```

See the source code of Solstice modules as practical examples.