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
import com.sphenon.basics.data.*;
import com.sphenon.basics.locating.*;

import com.sphenon.engines.generator.*;

import java.util.Vector;

import java.io.Reader;
import java.io.BufferedReader;
import java.util.Date;

public interface Template {
    public BufferedReader getReader (CallContext context);
    public String getPackageName(CallContext context);
    public String getClassName(CallContext context);
    public String getFullClassName(CallContext context);
    public String getTemplateTypeAlias(CallContext context);
    public long getLastModification(CallContext context);
    public Locator tryGetOrigin(CallContext context);

    /**
      Retrieves optionally a list of template class names which are in the
      same package as this given template.
      @return List of not fully qualified template class names or null, if not
              supported by implementation
     */
    public Vector<String> getPackageChilds(CallContext context);
}
