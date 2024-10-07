package com.sphenon.engines.generator.classes;

import com.sphenon.basics.context.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.configuration.*;

public class Configuration_BootstrapParser implements com.sphenon.engines.generator.classes.BootstrapParser.Config {

    protected Configuration configuration;

    protected Configuration_BootstrapParser (CallContext context) {
        configuration = Configuration.create(context, "com.sphenon.engines.generator.classes.BootstrapParser");
    }

    static public Configuration_BootstrapParser get (CallContext context) {
        return new Configuration_BootstrapParser(context);
    }

    public java.lang.String getTemplateTypeAlias(CallContext context, java.lang.String alias_name) {
        String entry = "TemplateTypeAlias." + alias_name + "";
        return configuration.get(context, entry, (java.lang.String) null);
    }
}
