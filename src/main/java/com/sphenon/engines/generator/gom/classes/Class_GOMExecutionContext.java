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
import com.sphenon.basics.data.*;
import com.sphenon.basics.encoding.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.gom.*;

import java.io.PrintWriter;
import java.util.Stack;

public class Class_GOMExecutionContext implements GOMExecutionContext {

    protected GOMExecutionContext parent_gom_execution_context;

    public Class_GOMExecutionContext(CallContext context, Generator generator, GOMExecutionContext parent_gom_execution_context) {
        this.generator = generator;
        this.parent_gom_execution_context = parent_gom_execution_context;
        if (this.parent_gom_execution_context != null) {
            this.setTextEncodingSteps(context, this.parent_gom_execution_context.getTextEncodingSteps(context));
            this.setEncodingSteps(context, this.parent_gom_execution_context.getEncodingSteps(context));
            this.indent = this.parent_gom_execution_context.getIndent(context);
        }
    }

    protected Generator generator;

    public Generator getGenerator(CallContext context) {
        return this.generator;
    }

    protected GeneratorOutputHandler output_handler;

    public GeneratorOutputHandler getOutputHandler (CallContext context) {
        return this.output_handler;
    }

    public void setOutputHandler (CallContext context, GeneratorOutputHandler output_handler) {
        this.output_handler = output_handler;
    }

    protected boolean text_mode       = false;
    protected boolean expression_mode = false;

    public PrintWriter getOutput (CallContext context, boolean use_text_mode) {
        if ( ! expression_mode && ! use_text_mode) {
            this.text_mode       = false;
            this.expression_mode = true;
            this.output_handler.getWriter(context).setEncodingSteps(context, this.encoding_steps);
        } else if ( ! text_mode && use_text_mode) {
            this.text_mode       = true;
            this.expression_mode = false;
            this.output_handler.getWriter(context).setEncodingSteps(context, this.text_encoding_steps);
        }
        return this.output_handler.getPrintWriter(context);
    }

    protected EncodingStep[] text_encoding_steps;

    public EncodingStep[] getTextEncodingSteps (CallContext context) {
        return this.text_encoding_steps;
    }

    public void setTextEncodingSteps (CallContext context, EncodingStep[] text_encoding_steps) {
        this.text_encoding_steps = text_encoding_steps;
        if (text_mode && this.output_handler != null) {
            this.output_handler.getWriter(context).setEncodingSteps(context, this.text_encoding_steps);
        }
    }

    protected EncodingStep[] encoding_steps;

    public EncodingStep[] getEncodingSteps (CallContext context) {
        return this.encoding_steps;
    }

    public void setEncodingSteps (CallContext context, EncodingStep[] encoding_steps) {
        this.encoding_steps = encoding_steps;
        if ( ! text_mode && this.output_handler != null) {
            this.output_handler.getWriter(context).setEncodingSteps(context, this.encoding_steps);
        }
    }

    protected String indent = "";

    public String getIndent(CallContext context) {
        return this.indent;
    }

    protected Stack<String> indent_stack;

    public void pushIndent(CallContext context, String postfix) {
        if (this.indent_stack == null) {
            this.indent_stack = new Stack<String>();
        }
        this.indent_stack.push(indent);
        indent += postfix;
    }

    public void popIndent(CallContext context) {
        if (this.indent_stack != null) {
            indent = this.indent_stack.pop();
        } else {
            CustomaryContext.create((Context)context).throwConfigurationError(context, "During generation, the indent stack is empty while requesting a pop operation");
            throw (ExceptionConfigurationError) null; // compiler insists
        }
    }
}
