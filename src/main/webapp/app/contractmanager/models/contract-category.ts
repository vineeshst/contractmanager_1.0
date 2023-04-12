export class ContractCategory {
  id: string | undefined;
  categoryName: string;
  categoryDescription: string;

  constructor(categoryName: string, categoryDescription: string) {
    this.categoryName = categoryName;
    this.categoryDescription = categoryDescription;
  }
}
