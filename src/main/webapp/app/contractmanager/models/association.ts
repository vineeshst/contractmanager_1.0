export class Association {
  associationName: string | undefined;
  associatedContractType: string | undefined;
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  allowInheritance: boolean;
  allowTwowayLinkage: boolean | undefined;
  allowMutipleInstance: boolean | undefined;
  allowPeerCreationWizard: boolean | undefined;
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  IsMandatory: boolean;
  useCustomNomenclature: boolean | undefined;
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  defineByRule: boolean;
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  inlineAssociation: boolean;

  constructor() {
    //do nothing
  }
}
