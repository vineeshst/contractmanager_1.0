import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgreementTileViewComponent } from './agreement-tile-view.component';

describe('AgreementTileViewComponent', () => {
  let component: AgreementTileViewComponent;
  let fixture: ComponentFixture<AgreementTileViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AgreementTileViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AgreementTileViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
