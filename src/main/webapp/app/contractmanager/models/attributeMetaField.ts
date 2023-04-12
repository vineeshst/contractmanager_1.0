export class AttributeMetaField {
  key: string;
  value: string;
  isEditable: boolean;

  constructor(key: string, value: string, isEditable: boolean) {
    this.key = key;
    this.value = value;
    this.isEditable = isEditable;
  }
}
