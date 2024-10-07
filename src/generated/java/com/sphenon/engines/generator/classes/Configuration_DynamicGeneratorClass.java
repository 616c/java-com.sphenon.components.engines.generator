package com.sphenon.engines.generator.classes;

import com.sphenon.basics.context.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.configuration.*;

public class Configuration_DynamicGeneratorClass implements com.sphenon.engines.generator.classes.DynamicGeneratorClass.Config {

    protected Configuration configuration;

    protected Configuration_DynamicGeneratorClass (CallContext context) {
        configuration = Configuration.create(context, "com.sphenon.engines.generator.classes.DynamicGeneratorClass");
    }

    static public Configuration_DynamicGeneratorClass get (CallContext context) {
        return new Configuration_DynamicGeneratorClass(context);
    }

    public boolean getDoGeneration(CallContext context) {
        String entry = "DoGeneration";
        return configuration.get(context, entry, true);
    }

    public boolean getDoCompilation(CallContext context) {
        String entry = "DoCompilation";
        return configuration.get(context, entry, true);
    }

    public boolean getTryToLoadAsResource(CallContext context) {
        String entry = "TryToLoadAsResource";
        return configuration.get(context, entry, true);
    }

    public boolean getUseExistingResourceUnconditionally(CallContext context) {
        String entry = "UseExistingResourceUnconditionally";
        return configuration.get(context, entry, false);
    }
}
