package com.sphenon.engines.generator.classes;

import com.sphenon.basics.context.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.configuration.*;

public class Configuration_Class_TemplateInstance implements com.sphenon.engines.generator.classes.Class_TemplateInstance.Config {

    protected Configuration configuration;

    protected Configuration_Class_TemplateInstance (CallContext context) {
        configuration = Configuration.create(context, "com.sphenon.engines.generator.classes.Class_TemplateInstance");
    }

    static public Configuration_Class_TemplateInstance get (CallContext context) {
        return new Configuration_Class_TemplateInstance(context);
    }

    public java.lang.String getTemplateInstancePath(CallContext context) {
        String entry = "TemplateInstancePath";
        return configuration.get(context, entry, (java.lang.String) null);
    }

    public void setTemplateInstancePath(CallContext context, java.lang.String template_instance_path) {
        configuration.set(context, "TemplateInstancePath", template_instance_path);
    }
}
