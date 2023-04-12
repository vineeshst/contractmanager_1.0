import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewUserGroupsComponent } from './view-user-groups.component';

describe('ViewUserGroupsComponent', () => {
  let component: ViewUserGroupsComponent;
  let fixture: ComponentFixture<ViewUserGroupsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewUserGroupsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewUserGroupsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
