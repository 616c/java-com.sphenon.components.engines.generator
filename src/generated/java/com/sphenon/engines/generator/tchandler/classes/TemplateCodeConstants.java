/* Generated By:JavaCC: Do not edit this line. TemplateCodeConstants.java */

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
package com.sphenon.engines.generator.tchandler.classes;

public interface TemplateCodeConstants {

  int EOF = 0;
  int WS = 1;
  int SIGNATURE = 2;
  int LOCTGT = 3;
  int TEMPLATE = 4;
  int ENDTEMPLATE = 5;
  int INSERT = 6;
  int POLYMORPHIC = 7;
  int OVERRIDE = 8;
  int BASE = 9;
  int IMPLEMENTS = 10;
  int NAME = 11;
  int LASTMODIFICATION = 12;
  int REQUIRES = 13;
  int PARTITION = 14;
  int ENDPARTITION = 15;
  int RECODING = 16;
  int ENDRECODING = 17;
  int END = 18;
  int CHARACTER_LITERAL = 19;
  int STRING_LITERAL = 20;
  int IDENTIFIER = 21;
  int OPPAR = 22;
  int CLPAR = 23;
  int OPBRC = 24;
  int CLBRC = 25;
  int OPBRK = 26;
  int CLBRK = 27;
  int LT = 28;
  int GT = 29;
  int COMMA = 30;
  int COLON = 31;
  int EQUAL = 32;
  int DOT = 33;
  int SLASH = 34;
  int ANY = 35;

  int DEFAULT = 0;
  int JCODE = 1;

  String[] tokenImage = {
    "<EOF>",
    "<WS>",
    "\"signature\"",
    "\"locatortarget\"",
    "<TEMPLATE>",
    "\"/#{template}\"",
    "<INSERT>",
    "\"polymorphic\"",
    "\"override\"",
    "\"base\"",
    "\"implements\"",
    "\"name\"",
    "\"lastmodification\"",
    "\"requires\"",
    "<PARTITION>",
    "\"/#{partition}\"",
    "<RECODING>",
    "\"/%%\"",
    "\"end\"",
    "<CHARACTER_LITERAL>",
    "<STRING_LITERAL>",
    "<IDENTIFIER>",
    "\"(\"",
    "\")\"",
    "\"{\"",
    "\"}\"",
    "\"[\"",
    "\"]\"",
    "\"<\"",
    "\">\"",
    "\",\"",
    "\":\"",
    "\"=\"",
    "\".\"",
    "\"/\"",
    "<ANY>",
  };

}
