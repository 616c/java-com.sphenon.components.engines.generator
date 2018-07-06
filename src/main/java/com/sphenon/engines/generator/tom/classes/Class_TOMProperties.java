package com.sphenon.engines.generator.tom.classes;

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

import com.sphenon.engines.generator.tom.*;
import com.sphenon.engines.generator.*;
import com.sphenon.engines.generator.classes.*;

import java.io.BufferedWriter;
import java.util.Vector;

public class Class_TOMProperties implements TOMProperties {

    public Class_TOMProperties (CallContext context) {
    }

    protected Recoding recoding;

    public Recoding getRecoding (CallContext context) {
        return this.recoding;
    }

    public void setRecoding (CallContext context, Recoding recoding) {
        this.recoding = recoding;
    }

    protected Recoding text_recoding;

    public Recoding getTextRecoding (CallContext context) {
        return this.text_recoding;
    }

    public void setTextRecoding (CallContext context, Recoding text_recoding) {
        this.text_recoding = text_recoding;
    }
}
