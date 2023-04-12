import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { IUser, User } from '../../../core/user/user.model';
import { SecurityGroup } from '../../models/security-group';
import { UserGroup } from '../../models/user-group';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Roles } from '../../shared/constants/user-role.constans';
import { MatDialog } from '@angular/material/dialog';
import { UserService } from '../../../core/user/user.service';
import { AccountService } from '../../../core/auth/account.service';
import { UserManagementService } from '../../user-management/user-management.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UserManagementService as AdminUserManagementService } from '../../../admin/user-management/service/user-management.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';
import { SessionStorageService } from 'ngx-webstorage';

@Component({
  selector: 'jhi-my-settings',
  templateUrl: './my-settings.component.html',
  styleUrls: ['./my-settings.component.scss'],
})
export class MySettingsComponent implements OnInit {
  user!: User;
  userId?: string;
  currentUser: IUser = new User();
  securityGroups: SecurityGroup[] = [];
  userGroups: UserGroup[] = [];

  userForm = this.fb.group({
    id: [],
    login: [''],
    firstName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    lastName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: ['', [Validators.minLength(8), Validators.maxLength(15)]],
    externalIdentifier: [''],
    typeOfUser: [''],
    organisationUnit: [{ value: 'Default', disabled: true }],
    administrator: [false],
    authorisedSignatory: [false],
    departmentId: [''],
    signatoryAgent: [false],
    hierarchyLevel: [''],
    securityGroup: [null],
    expiryDate: [''],
    userGroup: [null],
    supervisorUser: [''],
    preference: this.fb.group({
      recordsPerPage: [''],
      gridChoice: [''],
      recordsLayout: [''],
      dashboardLayout: [''],
      searchPanelVisible: [false],
      openRecordInSameTab: [false],
      language: [''],
      timeZone: [''],
      dateFormat: [''],
      timeFormat: [''],
      preferredCurrency: [''],
    }),
  });

  get preferenceForm(): FormGroup | null {
    return this.userForm.get('preference') as FormGroup;
  }

  get firstName(): FormControl {
    return this.userForm.get('firstName') as FormControl;
  }

  get lastName(): FormControl {
    return this.userForm.get('lastName') as FormControl;
  }

  get email(): FormControl {
    return this.userForm.get('email') as FormControl;
  }

  teamFields: string[] = [Roles.SecondaryOwner];

  @ViewChild('teamDialog') teamDialog!: TemplateRef<any>;

  constructor(
    private fb: FormBuilder,
    private dialog: MatDialog,
    private userService: UserService,
    private accoutService: AccountService,
    private userManagementService: UserManagementService,
    private route: ActivatedRoute,
    private adminUserManagementService: AdminUserManagementService,
    private snackBar: MatSnackBar,
    private router: Router,
    private translateService: TranslateService,
    private sessionStorageService: SessionStorageService
  ) {}

  ngOnInit(): void {
    this.accoutService.identity().subscribe(account => {
      this.currentUser = Object.assign(this.currentUser, account);
      this.updateUserForm(this.currentUser);
    });
    this.userManagementService.getSecurityGroups().subscribe(securityGroups => {
      this.securityGroups = securityGroups;
    });

    this.userManagementService.getUserGroups().subscribe(userGroups => {
      this.userGroups = userGroups;
    });
  }
  updateUserForm(user: any): void {
    this.userForm.patchValue(Object.assign({}, user));
    this.userId = user.id;
    this.user = user;
  }

  save(): void {
    this.updateUser(this.user);

    this.userService.update(this.user).subscribe(res => {
      this.showMessage(`User is updated successfully!`, 'Ok');
      this.ngOnInit();
    });
  }
  changeLanguage(event: any): void {
    const languageKey = event.value;
    this.sessionStorageService.store('locale', languageKey);
    this.translateService.use(languageKey);
  }

  private updateUser(user?: User): void {
    user = Object.assign(user, this.userForm.value);
    user!.login = this.email.value;
    user!.langKey = user?.preference?.language;
  }

  private showMessage(message: string, action: string): void {
    this.snackBar.open(message, action, {
      duration: 2000,
    });
  }
}
