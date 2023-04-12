import { Component, OnInit } from '@angular/core';
import { SecurityEntities } from '../../shared/constants/security-entity.constants';
import { Privileges } from '../../models/security-group';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'jhi-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent {
  get securityEntity(): typeof SecurityEntities {
    return SecurityEntities;
  }

  get privilege(): typeof Privileges {
    return Privileges;
  }
}
