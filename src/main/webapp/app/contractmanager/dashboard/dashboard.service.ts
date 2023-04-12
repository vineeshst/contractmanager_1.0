import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { Notif } from '../models/notification';
import { Task } from '../models/task';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  public resourceUrl = SERVER_API_URL + 'api/dashboard';

  constructor(private http: HttpClient) {}

  getMyTasks(): Observable<any> {
    const getMyTasks = '/my-tasks';
    return this.http.get<Task>(`${this.resourceUrl}${getMyTasks}`);
  }

  getDashbaordCount(): Observable<any> {
    const getCount = '/count';
    return this.http.get<any>(`${this.resourceUrl}${getCount}`);
  }

  getCurrentUserNotifications(): Observable<Notif[]> {
    const getNotifications = '/notifications';
    return this.http.get<Notif[]>(`${this.resourceUrl}${getNotifications}`);
  }

  markNotificationAsRead(notificationId: string): Observable<HttpResponse<string>> {
    const markRead = '/markRead/' + notificationId;
    return this.http.put<HttpResponse<string>>(`${this.resourceUrl}${markRead}`, {});
  }

  clearAllNotifications(): Observable<HttpResponse<string>> {
    const clear = '/notifications/clear';
    return this.http.put<HttpResponse<string>>(`${this.resourceUrl}${clear}`, {});
  }
}
