import { Component } from '@angular/core';

import { Privileges } from '../../models/security-group';
import { SecurityEntities } from '../../shared/constants/security-entity.constants';

@Component({
  selector: 'jhi-agreement-tile-view',
  templateUrl: './agreement-tile-view.component.html',
  styleUrls: ['./agreement-tile-view.component.scss'],
})
export class AgreementTileViewComponent {
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
