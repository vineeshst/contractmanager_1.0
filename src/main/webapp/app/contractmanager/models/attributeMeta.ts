export class AttributeOptions {
  isGlobal = false;
  isDefault = false;
  isEditable = false;
  isMandatory = false;
  defaultByRule = false;
  isInherit = false;
  tempateCreation = false;
  isSearchable = false;
  isConditional = false;
  isLookup = false;
  isMultiSelect = false;
  hasLookupFilter = false;
  isCasacde = false;
  justificationRequired = false;
  isSupersedableByAmendments = false;
  isSupersedableByAssignments = false;
  isSupersedableByTermaination = false;
  isInheritOnAmendments = false;
  isDependsOnValueByReference = false;
  enableExpressions = false;
  isAiDiscoverable = false;
}

export class AttributeMeta {
  attributeName: string;
  displayName = '';
  pageName = '';
  dataType = '';
  group = '';
  helpMessage = '';
  source = '';
  dependsOnAttribute = '';
  attributeOptions: AttributeOptions = new AttributeOptions();
  constructor(attributeName: string) {
    this.attributeName = attributeName;
  }
}
