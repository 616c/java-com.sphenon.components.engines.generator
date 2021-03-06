/* Generated By:JavaCC: Do not edit this line. JavaTemplateTextPPConstants.java */

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

public interface JavaTemplateTextPPConstants {

  int EOF = 0;
  int TEMPLATE = 1;
  int REQUIRES = 2;
  int IDENTIFIER = 3;
  int DSHCLO = 4;
  int OPEDSH = 5;
  int EQUCLO = 6;
  int OPEEQU = 7;
  int OPTPL = 8;
  int CLTPL = 9;
  int LT = 10;
  int GT = 11;
  int COMMA = 12;
  int DOT = 13;
  int EQ = 14;
  int AT2 = 15;
  int WS = 16;
  int NL = 17;
  int ANY = 18;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\"template\"",
    "\"requires\"",
    "<IDENTIFIER>",
    "\"\\\\-\\\\>\"",
    "\"\\\\<\\\\-\"",
    "\"\\\\=\\\\>\"",
    "\"\\\\<\\\\=\"",
    "\"<-\"",
    "\"->\"",
    "\"<\"",
    "\">\"",
    "\",\"",
    "\".\"",
    "\"=\"",
    "\"@@\"",
    "<WS>",
    "\"\\n\"",
    "<ANY>",
  };

}
