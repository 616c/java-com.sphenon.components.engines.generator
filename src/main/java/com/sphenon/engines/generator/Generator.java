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

public interface Generator {
    /**
       Performs actual generation process, based on given arguments.

       @param output_handler Receives output of generator, there are several
                             subclasses available to write output to a file or
                             capture it in memory
       @param arguments      Passed to generator
    */
    public GeneratorOutputHandler generate (CallContext context, GeneratorOutputHandler output_handler, Object... arguments);

    /**
       Provides information about the accepted arguments of this generator

       @return Vector of formal arguments
     */
    public Vector<FormalArgument> getSignature(CallContext context);

    /**
       Calculates, based upon given arguments, a suggested instance name for
       the generation process based upon that same arguments.

       @param arguments As would be passed to generator
       @return An instance name, which is (more or less) unique with respect
               to the template name and the given arguments (for simple values)
     */
    public String getInstanceName (CallContext context, Object... arguments);

    /**
       Calculates, based upon given arguments, the last modification that
       possibly influences the output, not considering modifications of
       the template source.

       @param arguments As would be passed to generator
       @return The last influencing modification of the given arguments,
               or null if this information is not available
     */
    public java.util.Date getLastDataModification (CallContext context, Object... arguments);

    /**
       Retrieves associated meta data by the class of that data and based on
       optional arguments as they would be passed to the generate method. The
       meta data may, of course, sometimes be independent of that
       arguments. The instance_level argument therefor indicates if the
       additional arguments will be accessed.

       @param meta_data_class The class of the requested meta data
       @param instance_level  If true, the additional arguments will be
                              accessed and made available to the code that
                              evaluates the meta data; if false, the
                              respective arguments in the meta data code will
                              be null; so care has to be taken that only class
                              level meta data will be accessed when this
                              argument is false
       @return An instance of the class as given by the parameter
               meta_data_class, optionally based upon the additional
               arguments, or null if no such meta data exists
     */
    public<T> T getMetaData (CallContext context, Class<T> meta_data_class, boolean instance_level, Object... arguments);
}
