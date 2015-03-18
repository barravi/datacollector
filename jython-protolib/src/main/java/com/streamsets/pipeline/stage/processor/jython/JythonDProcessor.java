/**
 * (c) 2014 StreamSets, Inc. All rights reserved. May not
 * be copied, modified, or distributed in whole or part without
 * written consent of StreamSets, Inc.
 */
package com.streamsets.pipeline.stage.processor.jython;

import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.ConfigGroups;
import com.streamsets.pipeline.api.GenerateResourceBundle;
import com.streamsets.pipeline.api.Processor;
import com.streamsets.pipeline.api.StageDef;
import com.streamsets.pipeline.api.ValueChooser;
import com.streamsets.pipeline.configurablestage.DProcessor;
import com.streamsets.pipeline.stage.processor.scripting.ProcessingMode;
import com.streamsets.pipeline.stage.processor.scripting.ProcessingModeChooserValues;

@GenerateResourceBundle
@StageDef(
    version = "1.0.0",
    label = "Jython 2.7",
    description = "Jython script processor",
    icon="jython.png"
)
@ConfigGroups(Groups.class)
public class JythonDProcessor extends DProcessor {

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.MODEL,
      defaultValue = "RECORD",
      label = "Record Processing Mode",
      description = "If 'Record by Record' the processor takes care of record error handling, if 'Record batch' " +
                    "the Jython script must take care of record error handling",
      displayPosition = 10,
      group = "JYTHON"
  )
  @ValueChooser(ProcessingModeChooserValues.class)
  public ProcessingMode processingMode;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      defaultValue = "",
      label = "Script",
      description = "Jython script",
      displayPosition = 20,
      group = "JYTHON"
  )
  public String script;

  @Override
  protected Processor createProcessor() {
    return new JythonProcessor(processingMode, script);
  }

}