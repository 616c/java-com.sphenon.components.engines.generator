package com.sphenon.engines.generator.gom.classes;

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
import com.sphenon.basics.encoding.*;
import com.sphenon.basics.data.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.gom.*;

import java.io.Writer;
import java.io.PrintWriter;

import java.util.Stack;

public class GOMRecoding extends Class_GOMNode {

    // Configuration ------------------------------------------------------------------

    // Construction -------------------------------------------------------------------

    public GOMRecoding(CallContext context, GOMNode parent, int id) {
        super(context, parent, id);
    }

    // Attributes ---------------------------------------------------------------------

    protected EncodingStep[] encoding_steps;

    public EncodingStep[] getEncodingSteps (CallContext context) {
        return this.encoding_steps;
    }

    public void setEncodingSteps (CallContext context, EncodingStep[] encoding_steps) {
        this.encoding_steps = encoding_steps;
    }

    protected EncodingStep[] text_encoding_steps;

    public EncodingStep[] getTextEncodingSteps (CallContext context) {
        return this.text_encoding_steps;
    }

    public void setTextEncodingSteps (CallContext context, EncodingStep[] text_encoding_steps) {
        this.text_encoding_steps = text_encoding_steps;
    }

    // Internal -----------------------------------------------------------------------

    protected class MyLocalVariables extends Class_GOMLocalVariables {
        public EncodingStep[] previous_encoding_steps;
        public EncodingStep[] previous_text_encoding_steps;
        public boolean got_steps;
    }

    // Operations ---------------------------------------------------------------------

    // --------------------------------------------------------------------------------

    public boolean generate (CallContext context, GOMProcessor gom_processor, GOMExecutionContext gom_execution_context, GeneratorOutputHandler output_handler, Object... arguments) {

        MyLocalVariables local = (MyLocalVariables) gom_processor.getLocalVariables(context);
        if (local == null) {
            local = new MyLocalVariables();
            gom_processor.setLocalVariables(context, local);
            return false;
        } else if (local.got_steps == false) {

            if (this.encoding_steps != null) {
                local.previous_encoding_steps = ((Class_GOMExecutionContext) gom_execution_context).getEncodingSteps(context);
                ((Class_GOMExecutionContext) gom_execution_context).setEncodingSteps(context, this.merge(context, this.encoding_steps, local.previous_encoding_steps));
            }

            if (this.text_encoding_steps != null) {
                local.previous_text_encoding_steps = ((Class_GOMExecutionContext) gom_execution_context).getTextEncodingSteps(context);
                ((Class_GOMExecutionContext) gom_execution_context).setTextEncodingSteps(context, this.merge(context, this.text_encoding_steps, local.previous_text_encoding_steps));
            }

            local.got_steps = true;
        } else {
            local.index++;
        }

        if (local.index < this.getChildNodes(context).size()) {
            gom_processor.push(context, this.getChildNodes(context).elementAt(local.index), gom_execution_context, output_handler, arguments);
        } else {
            gom_processor.setLocalVariables(context, null);
            gom_processor.pop(context);

            if (this.encoding_steps != null) {
                ((Class_GOMExecutionContext) gom_execution_context).setEncodingSteps(context, local.previous_encoding_steps);
            }

            if (this.text_encoding_steps != null) {
                ((Class_GOMExecutionContext) gom_execution_context).setTextEncodingSteps(context, local.previous_text_encoding_steps);
            }
        }
        return true;
    }
    
    // helper -------------------------------------------------------------------

    static public boolean isComplete(CallContext context, EncodingStep[] steps) {
        int size = (steps == null ? 0 : steps.length);
        EncodingStep senc = (size > 1 ? steps[0] : null);
        EncodingStep tenc = (size > 2 ? steps[size-1] : null);
        return (    size > 2
                 && senc != null && senc.getEncoding(context) != null
                 && tenc != null && tenc.getEncoding(context) != null
               );
    }

    static protected EncodingStep[] default_steps = {
        new EncodingStep(null, Encoding.UTF8),
        new EncodingStep(null, Encoding.UTF8)
    };

    static public EncodingStep[] merge(CallContext context, EncodingStep[] steps, EncodingStep[] parent_steps) {
        if (steps != null && isComplete(context, steps)) { return steps; }
        if (parent_steps == null) {
            parent_steps = default_steps;
        }

        EncodingStep[] result = null;

        EncodingStep my_senc    = (    steps.length > 0
                                    && steps[0] != null
                                    && steps[0].getEncoding(context) != null
                                  ? steps[0] : null);
        EncodingStep my_tenc    = (    steps.length > 1
                                    && steps[1] != null
                                    && steps[1].getEncoding(context) != null
                                  ? steps[1] : null);
        EncodingStep other_senc = (    parent_steps.length > 0
                                    && parent_steps[0] != null
                                    && parent_steps[0].getEncoding(context) != null
                                  ? parent_steps[0] : null);
        EncodingStep other_tenc = (    parent_steps.length > 1
                                    && parent_steps[1] != null
                                    && parent_steps[1].getEncoding(context) != null
                                  ? parent_steps[1] : null);

        if (my_senc == null && other_senc != null) {
            result = new EncodingStep[steps.length];
            for (int i=0; i<steps.length; i++) {
                result[i] = steps[i];
            }
            result[0] = other_senc;
        }

        if (my_tenc == null && other_tenc != null) {
            if (result == null) {
                result = new EncodingStep[steps.length];
                for (int i=0; i<steps.length; i++) {
                    result[i] = steps[i];
                }
            }
            result[steps.length-1] = other_senc;
        }

        return (result == null ? steps : result);
    }
}
