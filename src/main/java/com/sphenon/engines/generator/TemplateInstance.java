package com.sphenon.engines.generator;

/****************************************************************************
  Copyright 2001-2018 Sphenon GmbH

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations
  under the License.
*****************************************************************************/

import com.sphenon.basics.context.*;
import com.sphenon.basics.context.classes.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;

import java.util.Vector;
import java.util.Map;

public interface TemplateInstance {

    public String getTemplateName (CallContext context);

    public Object[] getArguments (CallContext context);

    public String getTemplateInstanceExpression (CallContext context);

    public void create(CallContext context, String package_name, Vector<String> imports, JavaTemplateTraits traits, boolean recursive, Map<String,TemplateInstance> already_processed, TIFilter filter, boolean keep_unmodified_files);

    static public class TIFilter {
        boolean include_java_library_types;

        public TIFilter(CallContext context) {
            this.include_java_library_types = false;
        }

        public TIFilter(CallContext context, boolean include_java_library_types) {
            this.include_java_library_types = include_java_library_types;
        }

        public boolean include(CallContext context, TemplateInstance template_instance) {
            boolean do_include = false;
            for (Object argument : template_instance.getArguments(context)) {
                if (argument != null) {
                    if (argument instanceof String) {
                        if (    this.include_java_library_types
                             || ((String)argument).matches("((Boolean)|(boolean)|(Byte)|(byte)|(Character)|(char)|(Short)|(short)|(Integer)|(int)|(Long)|(long)|(Float)|(float)|(Double)|(double)|(Object)|(String)|(Date))") == false) {
                            return true;
                        }
                    } else if (argument instanceof TemplateInstance) {
                        if (this.include(context, (TemplateInstance)argument)) {
                            return true;
                        }
                    }
                }
            }
            
            return false;
        }
    }
}
