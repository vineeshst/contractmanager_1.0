import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from './sidebar/sidebar.component';
import { HeaderComponent } from './header/header.component';
import { MaterialModule } from '../../shared/material.module';
import { AvatarModule } from 'ngx-avatar';
import { RouterModule } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { NotificationsComponent } from './notifications/notifications.component';

@NgModule({
  declarations: [SidebarComponent, HeaderComponent, NotificationsComponent],
  imports: [CommonModule, MaterialModule, AvatarModule, RouterModule, SharedModule],
  exports: [SidebarComponent, HeaderComponent, NotificationsComponent],
})
export class LayoutsModule {}
