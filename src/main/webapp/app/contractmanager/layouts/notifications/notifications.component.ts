import { KeyValue } from '@angular/common';
import { HttpStatusCode } from '@angular/common/http';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

import { DashboardService } from '../../dashboard/dashboard.service';
import { EntityType } from '../../models/entity';
import { Notif, NotificationType } from './../../models/notification';

@Component({
  selector: 'jhi-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss'],
})
export class NotificationsComponent implements OnInit {
  notificationMap?: Map<string, Notif[]>;
  sortedMap?: Map<string, Notif[]>;
  today = new Date();
  yesterday = new Date();
  thisWeek = new Date();

  notificationMap$!: BehaviorSubject<Map<string, Notif[]>>;

  @Output() toggle = new EventEmitter<any>();

  @Output() hasNotification = new EventEmitter<boolean>();

  constructor(private dashboardService: DashboardService, private router: Router) {
    this.yesterday.setDate(this.today.getDate() - 1);
    this.thisWeek.setDate(this.today.getDate() - 7);
  }

  ngOnInit(): void {
    this.dashboardService.getCurrentUserNotifications().subscribe(notifications => {
      this.notificationMap = new Map<string, Notif[]>();
      this.notificationMap$ = new BehaviorSubject(this.notificationMap);

      notifications.forEach(n => {
        const notificationItem = this.buildNotification(n);

        const day = this.getDay(new Date(n.date));
        if (this.notificationMap?.has(day)) {
          this.notificationMap.get(day)?.push(notificationItem);
        } else {
          this.notificationMap?.set(day, [notificationItem]);
        }
      });
      this.notify();
    });
  }

  compareFunction(a: KeyValue<string, Notif[]>, b: KeyValue<string, Notif[]>): number {
    if (a.key === 'Today') {
      return 1;
    }
    if (a.key === 'Yesterday') {
      return 1;
    }
    if (a.key === 'This Week') {
      return 0;
    }
    if (a.key === 'Others') {
      return 0;
    }
    return 0;
  }

  getDateFormat(day: string): string {
    switch (day) {
      case 'Today':
        return 'h:mm';
      case 'Yesterday':
        return 'h:mm';
      case 'This Week':
        return 'EEE';
      case 'Others':
        return 'MMM d';
      default:
        return 'MMM d';
    }
  }

  handleClick(notification: Notif): void {
    this.close();

    switch (notification.entityType) {
      case EntityType.ContractType:
        this.router.navigate(['/contractmanager/configuration/view-contract-type/agreement/', notification.entityId]);
        break;

      case EntityType.Agreement:
        this.router.navigate(['/contractmanager/agreement/add-contract-agreement/', notification.entityId]);
        break;

      case EntityType.Template:
        this.router.navigate(['/contractmanager/template/add-contract-template/', notification.entityId]);
        break;

      case EntityType.Clause:
        this.router.navigate(['/contractmanager/clause/add-contract-clause/', notification.entityId]);
        break;

      default:
        this.router.navigate(['/404']);
    }

    if (!notification.read) {
      this.markNotificationAsRead(notification.id, notification.date);
    }
  }

  markNotificationAsRead(notificationId: string, date: Date): void {
    this.dashboardService.markNotificationAsRead(notificationId).subscribe(response => {
      if (response.status === HttpStatusCode.Ok) {
        const map = this.notificationMap?.get(this.getDay(new Date(date)));
        const notif = map?.find(x => x.id === notificationId);
        if (notif) {
          notif.read = true;
        }
      }
    });
  }

  close(): void {
    this.toggle.emit();
  }

  clearAll(): void {
    if (this.notificationMap && this.notificationMap.size > 0) {
      this.dashboardService.clearAllNotifications().subscribe(() => {
        this.notificationMap?.clear();
        this.notify();
      });
    }
  }

  private notify(): void {
    if (this.notificationMap) {
      this.notificationMap$.next(this.notificationMap);
    } else {
      this.notificationMap$.next(new Map<string, Notif[]>());
    }
  }

  private getDay(day: Date): string {
    const date = new Date(day);
    if (this.compareDate(date, this.today, 'eq')) {
      return 'Today';
    } else if (this.compareDate(date, this.yesterday, 'eq')) {
      return 'Yesterday';
    } else if (this.compareDate(date, this.thisWeek, 'lt')) {
      return 'This Week';
    } else {
      return 'Others';
    }
  }

  private compareDate(d1: Date, d2: Date, operator: string): boolean {
    if (d1.getMonth() !== d2.getMonth() || d1.getFullYear() !== d2.getFullYear()) {
      return false;
    }

    if (operator === 'eq') {
      return d1.getDate() === d2.getDate();
    } else {
      return d1.getDate() >= d2.getDate() && this.today.getDay() > d1.getDay();
    }
  }

  private buildNotification(notification: Notif): Notif {
    const entityType = this.getEntityType(notification.entityType);

    if (!notification.read) {
      this.hasNotification.emit(true);
    }

    switch (notification.type) {
      case NotificationType.Modified: {
        notification.message = ' has modified the ' + entityType;
        break;
      }
      case NotificationType.SentForApproval: {
        notification.message = ' has requested for approval for the ' + entityType;
        break;
      }
      case NotificationType.Approved: {
        notification.message = ' has approved the ' + entityType;
        break;
      }
      case NotificationType.Rejected: {
        notification.message = ' has rejected the ' + entityType;
        break;
      }
      case NotificationType.Created: {
        notification.message = ' has created the ' + entityType;
        break;
      }
      // case NotificationType.Executed: {
      //   notification.message = ' has rejected the '+entityType;
      //   break;
      // }
      // case NotificationType.Expired: {
      //   notification.message = ' has rejected the '+entityType;
      //   break;
      // }
      default:
        return notification;
    }
    return notification;
  }

  private getEntityType(type: EntityType): string {
    switch (type) {
      case EntityType.Agreement:
        return 'contract agreement ';
      case EntityType.Clause:
        return 'clause ';
      case EntityType.ContractType:
        return 'contract type ';
      case EntityType.SecurityGroup:
        return 'security group ';
      case EntityType.Template:
        return 'template ';
      case EntityType.User:
        return 'user ';
      default:
        return 'entity ';
    }
  }
}
