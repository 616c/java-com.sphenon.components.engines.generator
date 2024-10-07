package com.sphenon.engines.generator.classes;

import com.sphenon.basics.context.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.configuration.*;

public class Configuration_Class_Template_InMemory implements com.sphenon.engines.generator.classes.Class_Template_InMemory.Config {

    protected Configuration configuration;

    protected Configuration_Class_Template_InMemory (CallContext context) {
        configuration = Configuration.create(context, "com.sphenon.engines.generator.classes.Class_Template_InMemory");
    }

    static public Configuration_Class_Template_InMemory get (CallContext context) {
        return new Configuration_Class_Template_InMemory(context);
    }

    public java.lang.String getSHAMatchCheckPath(CallContext context) {
        String entry = "SHAMatchCheckPath";
        return configuration.get(context, entry, (java.lang.String) null);
    }
}
