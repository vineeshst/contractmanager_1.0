import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestApprovalDialogComponent } from './request-approval-dialog.component';

describe('RequestApprovalDialogComponent', () => {
  let component: RequestApprovalDialogComponent;
  let fixture: ComponentFixture<RequestApprovalDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RequestApprovalDialogComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestApprovalDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
