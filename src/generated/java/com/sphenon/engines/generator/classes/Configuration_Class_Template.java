package com.sphenon.engines.generator.classes;

import com.sphenon.basics.context.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.configuration.*;

public class Configuration_Class_Template implements com.sphenon.engines.generator.classes.Class_Template.Config {

    protected Configuration configuration;

    protected Configuration_Class_Template (CallContext context) {
        configuration = Configuration.create(context, "com.sphenon.engines.generator.classes.Class_Template");
    }

    static public Configuration_Class_Template get (CallContext context) {
        return new Configuration_Class_Template(context);
    }

    public java.lang.String getTemplatePath(CallContext context) {
        String entry = "TemplatePath";
        return configuration.get(context, entry, (java.lang.String) null);
    }

    public void setTemplatePath(CallContext context, java.lang.String template_path) {
        configuration.set(context, "TemplatePath", template_path);
    }

    public java.lang.String getTemplatePackagePath(CallContext context) {
        String entry = "TemplatePackagePath";
        return configuration.get(context, entry, (java.lang.String) null);
    }

    public void setTemplatePackagePath(CallContext context, java.lang.String template_package_path) {
        configuration.set(context, "TemplatePackagePath", template_package_path);
    }
}
