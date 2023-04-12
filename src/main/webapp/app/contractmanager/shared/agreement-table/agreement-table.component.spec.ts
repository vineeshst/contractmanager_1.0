import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgreementTableComponent } from './agreement-table.component';

describe('AgreementTableComponent', () => {
  let component: AgreementTableComponent;
  let fixture: ComponentFixture<AgreementTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AgreementTableComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AgreementTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
