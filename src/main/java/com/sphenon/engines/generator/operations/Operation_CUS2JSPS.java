package com.sphenon.engines.generator.operations;

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
import com.sphenon.basics.configuration.*;
import com.sphenon.basics.message.*;
import com.sphenon.basics.exception.*;
import com.sphenon.basics.notification.*;
import com.sphenon.basics.customary.*;
import com.sphenon.basics.monitoring.*;
import com.sphenon.basics.operations.*;
import com.sphenon.basics.operations.classes.*;
import com.sphenon.engines.factorysite.*;
import com.sphenon.engines.factorysite.factories.*;
import com.sphenon.basics.data.*;

import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;
import com.sphenon.engines.generator.returncodes.*;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;

public class Operation_CUS2JSPS implements Operation {

    public Operation_CUS2JSPS (CallContext context) {
    }

    protected String cus_file;

    public String getCUSFile (CallContext context) {
        return this.cus_file;
    }

    public void setCUSFile (CallContext context, String cus_file) {
        this.cus_file = cus_file;
    }

    protected String jsps_file;

    public String getJSPSFile (CallContext context) {
        return this.jsps_file;
    }

    public void setJSPSFile (CallContext context, String jsps_file) {
        this.jsps_file = jsps_file;
    }

    public Execution execute (CallContext context) {
        return execute(context, null);
    }

    public Execution execute (CallContext context, com.sphenon.basics.data.DataSink<Execution> execution_sink) {
        Execution execution = null;

        try {
            BufferedWriter bw = null;
            OutputStreamWriter osw = null;
            FileOutputStream fos = null;
            bw = new BufferedWriter(osw = new OutputStreamWriter(fos = new FileOutputStream(this.jsps_file), "UTF-8"));

            Template t = new Class_Template(context, "dummy.path.DummyName", new File(this.cus_file));

            TemplateParser tp = (TemplateParser) Factory_Aggregate.construct(context, "com/sphenon/engines/generator/parsers/Converter-CUS-2-JSPS", "Writer", bw);

            tp.parse(context, t, null, null);

            bw.close();
            osw.close();
            fos.close();

            execution = Class_Execution.createExecutionSuccess(context);
        } catch (IOException ioe) {
            execution = Class_Execution.createExecutionFailure(context, ioe);
        } catch (InvalidTemplateSyntax its) {
            execution = Class_Execution.createExecutionFailure(context, its);
        } catch (NoSuchTemplate nst) {
            execution = Class_Execution.createExecutionFailure(context, nst);
        }

        if (execution_sink != null) { execution_sink.set(context, execution); }
        return execution;
    }

}
