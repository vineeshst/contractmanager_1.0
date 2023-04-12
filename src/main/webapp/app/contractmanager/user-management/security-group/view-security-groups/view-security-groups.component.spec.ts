import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewSecurityGroupsComponent } from './view-security-groups.component';

describe('ViewSecurityGroupsComponent', () => {
  let component: ViewSecurityGroupsComponent;
  let fixture: ComponentFixture<ViewSecurityGroupsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewSecurityGroupsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewSecurityGroupsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
