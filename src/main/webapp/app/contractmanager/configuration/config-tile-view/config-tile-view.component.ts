import { Component } from '@angular/core';
import { Privileges } from '../../models/security-group';
import { SecurityEntities } from '../../shared/constants/security-entity.constants';

@Component({
  selector: 'jhi-tile-view',
  templateUrl: './config-tile-view.component.html',
  styleUrls: ['./config-tile-view.component.scss'],
})
export class ConfigTileViewComponent {
  get securityEntity(): typeof SecurityEntities {
    return SecurityEntities;
  }

  get privilege(): typeof Privileges {
    return Privileges;
  }

  constructor() {
    //do nothing
  }
}
