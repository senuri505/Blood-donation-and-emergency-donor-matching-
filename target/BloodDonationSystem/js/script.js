// Global Session User Property (guarantees currentUser is never undefined anywhere in any scope or page)
function getCurrentUser() {
    try {
        return JSON.parse(sessionStorage.getItem('hemolink_session_user') || 'null');
    } catch (e) {
        return null;
    }
}

if (typeof window !== 'undefined') {
    try {
        Object.defineProperty(window, 'currentUser', {
            get: function() {
                return getCurrentUser();
            },
            configurable: true,
            enumerable: true
        });
    } catch (e) {}
}

// Initialize Local Mock DB if Servlet is unavailable (e.g. static XAMPP Apache testing)
function initMockDB() {
    // Clear legacy mock data if Indian sample data is detected
    if (localStorage.getItem('hemolink_users') && localStorage.getItem('hemolink_users').includes('Arjun Mehta')) {
        localStorage.removeItem('hemolink_users');
        localStorage.removeItem('hemolink_donors');
        localStorage.removeItem('hemolink_hospitals');
        localStorage.removeItem('hemolink_blood_units');
        localStorage.removeItem('hemolink_donations');
        localStorage.removeItem('hemolink_requests');
        localStorage.removeItem('hemolink_notifications');
    }

    if (!localStorage.getItem('hemolink_users')) {
        const users = [
            { id: 1, username: 'admin', password: 'admin', role: 'admin', name: 'System Admin', email: 'admin@hemolink.lk', phone: '0770000001', createdAt: '2026-06-19' },
            { id: 2, username: 'donor1', password: 'donor', role: 'donor', name: 'Kasun Perera', email: 'kasun@mail.lk', phone: '0771234567', createdAt: '2026-06-19' },
            { id: 3, username: 'donor2', password: 'donor', role: 'donor', name: 'Chamari Fernando', email: 'chamari@mail.lk', phone: '0712345678', createdAt: '2026-06-19' },
            { id: 4, username: 'donor3', password: 'donor', role: 'donor', name: 'Nuwan Silva', email: 'nuwan@mail.lk', phone: '0763456789', createdAt: '2026-06-19' },
            { id: 5, username: 'donor4', password: 'donor', role: 'donor', name: 'Dilani Jayasinghe', email: 'dilani@mail.lk', phone: '0754567890', createdAt: '2026-06-19' },
            { id: 6, username: 'donor5', password: 'donor', role: 'donor', name: 'Kusal Mendis', email: 'kusal@mail.lk', phone: '0705678901', createdAt: '2026-06-19' },
            { id: 7, username: 'donor6', password: 'donor', role: 'donor', name: 'Tharushi Ranasinghe', email: 'tharushi@mail.lk', phone: '0726789012', createdAt: '2026-06-19' },
            { id: 8, username: 'donor_await', password: 'donor', role: 'donor', name: 'Ruwan Bandara', email: 'ruwan@mail.lk', phone: '0787890123', createdAt: '2026-06-19' },
            { id: 9, username: 'hosp1', password: 'hospital', role: 'hospital', name: 'National Hospital Colombo', email: 'nhsl@hosp.lk', phone: '0112691111', createdAt: '2026-06-19' },
            { id: 10, username: 'hosp2', password: 'hospital', role: 'hospital', name: 'Teaching Hospital Kandy', email: 'kandy@hosp.lk', phone: '0812222261', createdAt: '2026-06-19' },
            { id: 11, username: 'hosp_await', password: 'hospital', role: 'hospital', name: 'Lanka Hospitals Colombo', email: 'lanka@hosp.lk', phone: '0115430000', createdAt: '2026-06-19' }
        ];
        localStorage.setItem('hemolink_users', JSON.stringify(users));
    }

    if (!localStorage.getItem('hemolink_donors')) {
        const donors = [
            { id: 1, userId: 2, name: 'Kasun Perera', bloodType: 'O', rhFactor: '-', age: 28, gender: 'Male', city: 'Colombo', address: 'Cinnamon Gardens', weight: 72, phone: '0771234567', medicalConditions: 'None', lastDonationDate: null, isAvailable: true, totalDonations: 0, approvalStatus: 'approved' },
            { id: 2, userId: 3, name: 'Chamari Fernando', bloodType: 'A', rhFactor: '+', age: 32, gender: 'Female', city: 'Kandy', address: 'Peradeniya', weight: 58, phone: '0712345678', medicalConditions: 'None', lastDonationDate: '2024-09-15', isAvailable: true, totalDonations: 3, approvalStatus: 'approved' },
            { id: 3, userId: 4, name: 'Nuwan Silva', bloodType: 'B', rhFactor: '+', age: 25, gender: 'Male', city: 'Galle', address: 'Fort Main St', weight: 68, phone: '0763456789', medicalConditions: 'None', lastDonationDate: null, isAvailable: true, totalDonations: 1, approvalStatus: 'approved' },
            { id: 4, userId: 5, name: 'Dilani Jayasinghe', bloodType: 'AB', rhFactor: '+', age: 30, gender: 'Female', city: 'Colombo', address: 'Wellawatte', weight: 62, phone: '0754567890', medicalConditions: 'None', lastDonationDate: '2024-05-10', isAvailable: true, totalDonations: 2, approvalStatus: 'approved' },
            { id: 5, userId: 6, name: 'Kusal Mendis', bloodType: 'O', rhFactor: '+', age: 35, gender: 'Male', city: 'Gampaha', address: 'Negombo Rd', weight: 78, phone: '0705678901', medicalConditions: 'None', lastDonationDate: null, isAvailable: true, totalDonations: 4, approvalStatus: 'approved' },
            { id: 6, userId: 7, name: 'Tharushi Ranasinghe', bloodType: 'A', rhFactor: '-', age: 27, gender: 'Female', city: 'Kurunegala', address: 'Bauddhaloka Mawatha', weight: 54, phone: '0726789012', medicalConditions: 'None', lastDonationDate: null, isAvailable: true, totalDonations: 1, approvalStatus: 'approved' },
            { id: 7, userId: 8, name: 'Ruwan Bandara', bloodType: 'AB', rhFactor: '-', age: 40, gender: 'Male', city: 'Jaffna', address: 'Nallur', weight: 75, phone: '0787890123', medicalConditions: 'None', lastDonationDate: null, isAvailable: true, totalDonations: 0, approvalStatus: 'awaiting' }
        ];
        localStorage.setItem('hemolink_donors', JSON.stringify(donors));
    }

    if (!localStorage.getItem('hemolink_hospitals')) {
        const hospitals = [
            { id: 1, userId: 9, name: 'National Hospital Colombo', city: 'Colombo', address: 'E. W. Perera Mawatha', license: 'SL-2024-001', type: 'Government', approvalStatus: 'approved' },
            { id: 2, userId: 10, name: 'Teaching Hospital Kandy', city: 'Kandy', address: 'William Gopallawa Mawatha', license: 'SL-2024-015', type: 'Government', approvalStatus: 'approved' },
            { id: 3, userId: 11, name: 'Lanka Hospitals Colombo', city: 'Colombo', address: '578 Elvitigala Mawatha', license: 'SL-2024-088', type: 'Private', approvalStatus: 'awaiting' }
        ];
        localStorage.setItem('hemolink_hospitals', JSON.stringify(hospitals));
    }

    if (!localStorage.getItem('hemolink_blood_units')) {
        const units = [
            { id: 1, bloodType: 'O', rhFactor: '-', volumeMl: 450, collectedDate: '2025-01-05', expiresDate: '2025-02-16', donorId: 1, donorName: 'Kasun Perera', status: 'available' },
            { id: 2, bloodType: 'A', rhFactor: '+', volumeMl: 450, collectedDate: '2025-01-08', expiresDate: '2025-02-19', donorId: null, donorName: 'Walk-in', status: 'available' },
            { id: 3, bloodType: 'B', rhFactor: '+', volumeMl: 450, collectedDate: '2025-01-06', expiresDate: '2025-02-17', donorId: 3, donorName: 'Nuwan Silva', status: 'available' },
            { id: 4, bloodType: 'O', rhFactor: '+', volumeMl: 450, collectedDate: '2025-01-11', expiresDate: '2025-02-22', donorId: null, donorName: 'Walk-in', status: 'available' },
            { id: 5, bloodType: 'A', rhFactor: '-', volumeMl: 450, collectedDate: '2025-01-04', expiresDate: '2025-02-15', donorId: null, donorName: 'Walk-in', status: 'available' }
        ];
        localStorage.setItem('hemolink_blood_units', JSON.stringify(units));
    }

    if (!localStorage.getItem('hemolink_donations')) {
        const donations = [
            { id: 1, donorId: 2, donorName: 'Chamari Fernando', bloodType: 'A', rhFactor: '+', volumeMl: 450, donatedAt: '2024-09-15 10:30:00', hospitalName: 'National Hospital Colombo', hospitalId: 1, notes: 'Routine voluntary donation' },
            { id: 2, donorId: 3, donorName: 'Nuwan Silva', bloodType: 'B', rhFactor: '+', volumeMl: 450, donatedAt: '2025-01-06 14:00:00', hospitalName: 'Teaching Hospital Kandy', hospitalId: 2, notes: 'Voluntary blood drive' },
            { id: 3, donorId: 5, donorName: 'Kusal Mendis', bloodType: 'O', rhFactor: '+', volumeMl: 450, donatedAt: '2024-11-20 11:15:00', hospitalName: 'National Hospital Colombo', hospitalId: 1, notes: 'Emergency response donation' },
            { id: 4, donorId: 4, donorName: 'Dilani Jayasinghe', bloodType: 'AB', rhFactor: '+', volumeMl: 450, donatedAt: '2024-05-10 09:45:00', hospitalName: 'National Hospital Colombo', hospitalId: 1, notes: 'Voluntary donation' }
        ];
        localStorage.setItem('hemolink_donations', JSON.stringify(donations));
    }

    if (!localStorage.getItem('hemolink_requests')) {
        const requests = [
            { id: 1, patientName: 'Sahan Wickramasinghe', bloodType: 'O', rhFactor: '+', unitsNeeded: 2, urgency: 'critical', hospitalId: 1, hospitalName: 'National Hospital Colombo', city: 'Colombo', contactPerson: 'Dr. A. Wickramasinghe', phone: '0112691111', notes: 'Post-surgery transfusion', status: 'pending', matchedDonorIds: '1,5', donorResponses: '', createdAt: '2026-07-20 10:00:00' },
            { id: 2, patientName: 'Malini Perera', bloodType: 'A', rhFactor: '-', unitsNeeded: 1, urgency: 'urgent', hospitalId: 2, hospitalName: 'Teaching Hospital Kandy', city: 'Kandy', contactPerson: 'Dr. K. Jayawardena', phone: '0812222261', notes: 'Anemia during pregnancy', status: 'pending', matchedDonorIds: '6,1', donorResponses: '', createdAt: '2026-07-20 11:30:00' },
            { id: 3, patientName: 'Dinesh Gunawardena', bloodType: 'B', rhFactor: '+', unitsNeeded: 3, urgency: 'routine', hospitalId: 1, hospitalName: 'National Hospital Colombo', city: 'Colombo', contactPerson: 'Dr. N. Rajapaksha', phone: '0112691111', notes: 'Scheduled orthopedic surgery', status: 'pending', matchedDonorIds: '3', donorResponses: '', createdAt: '2026-07-20 14:15:00' }
        ];
        localStorage.setItem('hemolink_requests', JSON.stringify(requests));
    }

    if (!localStorage.getItem('hemolink_notifications')) {
        const notifs = [
            { id: 1, userId: 1, title: 'New Donor Registration', message: 'Ruwan Bandara registered as a donor and requires approval.', type: 'approval', isRead: false, createdAt: '2026-07-20 09:00:00' },
            { id: 2, userId: 1, title: 'New Hospital Registration', message: 'Lanka Hospitals Colombo registered as a hospital and requires approval.', type: 'approval', isRead: false, createdAt: '2026-07-20 09:15:00' },
            { id: 3, userId: 2, title: 'Welcome to HemoLink', message: 'Your donor profile is active. Thank you for registering as a blood donor.', type: 'info', isRead: true, createdAt: '2026-07-20 09:30:00' }
        ];
        localStorage.setItem('hemolink_notifications', JSON.stringify(notifs));
    } else {
        let notifs = JSON.parse(localStorage.getItem('hemolink_notifications') || '[]');
        const cleaned = notifs.filter(n => {
            if (n.userId == 1) {
                return n.type === 'approval' || n.type === 'registration';
            }
            return true;
        });
        localStorage.setItem('hemolink_notifications', JSON.stringify(cleaned));
    }

    if (!localStorage.getItem('hemolink_contact_messages')) {
        const initialContactMsgs = [
            { id: 1, name: 'Colombo Central Hospital Emergency Dept', email: 'emergency@colombohosp.lk', message: 'Urgent inquiry regarding bulk O- negative donor availability for weekend trauma clinic.', status: 'unread', createdAt: '2026-07-22 10:15:00' }
        ];
        localStorage.setItem('hemolink_contact_messages', JSON.stringify(initialContactMsgs));
    }
}

// Execute Mock DB Init
initMockDB();

// Dynamic URL resolver: checks if relative URL works with Servlet or fallback
async function apiGet(url) {
    try {
        const relativeUrl = window.location.pathname.includes('/admin/') || window.location.pathname.includes('/donor/') || window.location.pathname.includes('/hospital/') ? '../' + url : url;
        const res = await fetch(relativeUrl);
        if (res.ok && res.headers.get('content-type') && res.headers.get('content-type').includes('application/json')) {
            return await res.json();
        }
        throw new Error('Servlet not returning JSON, falling back to mock API');
    } catch (e) {
        // Fallback Mock Processing
        return handleMockGet(url);
    }
}

async function apiPost(url, data) {
    try {
        const relativeUrl = window.location.pathname.includes('/admin/') || window.location.pathname.includes('/donor/') || window.location.pathname.includes('/hospital/') ? '../' + url : url;
        const bodyData = new URLSearchParams();
        for (const key in data) {
            bodyData.append(key, data[key]);
        }
        const res = await fetch(relativeUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: bodyData
        });
        if (res.ok) {
            return await res.json();
        }
        throw new Error('Servlet error, falling back');
    } catch (e) {
        return handleMockPost(url, data);
    }
}

function redirectToLogin() {
    sessionStorage.removeItem('hemolink_session_user');
    const relativeLogin = window.location.pathname.includes('/admin/') || window.location.pathname.includes('/donor/') || window.location.pathname.includes('/hospital/') ? '../login.html' : 'login.html';
    window.location.href = relativeLogin;
}

function logoutUser() {
    sessionStorage.removeItem('hemolink_session_user');
    fetch('../LogoutServlet', { method: 'GET' })
        .catch(() => {})
        .finally(() => {
            const relativeLogin = window.location.pathname.includes('/admin/') || window.location.pathname.includes('/donor/') || window.location.pathname.includes('/hospital/') ? '../login.html' : 'login.html';
            window.location.href = relativeLogin;
        });
}

function isCompatibleBlood(donorType, recipientType) {
    if (!donorType || !recipientType) return false;
    donorType = donorType.trim();
    recipientType = recipientType.trim();
    const compatibility = {
        'A+': ['A+', 'A-', 'O+', 'O-'],
        'A-': ['A-', 'O-'],
        'B+': ['B+', 'B-', 'O+', 'O-'],
        'B-': ['B-', 'O-'],
        'AB+': ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'],
        'AB-': ['A-', 'B-', 'AB-', 'O-'],
        'O+': ['O+', 'O-'],
        'O-': ['O-']
    };
    return compatibility[recipientType] ? compatibility[recipientType].includes(donorType) : false;
}

async function requireAuth() {
    const protectedPath = window.location.pathname.includes('/admin/') || window.location.pathname.includes('/donor/') || window.location.pathname.includes('/hospital/');
    if (!protectedPath) return;

    let auth = null;
    try {
        auth = await apiGet('LoginServlet');
    } catch (e) {
        auth = null;
    }

    const currentUser = auth && auth.authenticated ? auth.user : JSON.parse(sessionStorage.getItem('hemolink_session_user') || 'null');
    if (!currentUser) {
        redirectToLogin();
        return;
    }

    if ((window.location.pathname.includes('/admin/') && currentUser.role !== 'admin') ||
        (window.location.pathname.includes('/donor/') && currentUser.role !== 'donor') ||
        (window.location.pathname.includes('/hospital/') && currentUser.role !== 'hospital')) {
        redirectToLogin();
    }
}

function updateDonorSidebar(donor) {
    if (!donor) return;
    const initials = donor.name ? donor.name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() : 'DN';
    const avatarEl = document.getElementById('donor-avatar');
    const nameEl = document.getElementById('donor-sidebar-name');
    const groupEl = document.getElementById('donor-sidebar-group');
    if (avatarEl) avatarEl.innerText = initials;
    if (nameEl) nameEl.innerText = donor.name || 'Donor';
    if (groupEl) groupEl.innerText = donor.bloodType + donor.rhFactor;
}

// Mock GET Router for Local Testing
function handleMockGet(url) {
    const urlObj = new URL('http://dummy/' + url);
    const servlet = urlObj.pathname.split('/').pop();
    const params = new URLSearchParams(urlObj.search);

    const currentUser = JSON.parse(sessionStorage.getItem('hemolink_session_user') || 'null');

    if (servlet === 'LoginServlet') {
        return currentUser ? { authenticated: true, user: currentUser } : { authenticated: false };
    }

    if (servlet === 'DonorServlet') {
        let donors = JSON.parse(localStorage.getItem('hemolink_donors') || '[]');
        const action = params.get('action') || 'list';
        if (action === 'awaiting') return donors.filter(d => d.approvalStatus === 'awaiting');
        if (action === 'me') return currentUser ? donors.find(d => d.userId === currentUser.id) || null : null;

        const city = params.get('city');
        const bloodType = params.get('bloodType') || params.get('recipientBloodType');
        const rhFactor = params.get('rhFactor') || params.get('recipientRh');
        const status = params.get('status');
        const availability = params.get('availability');

        if (action === 'search' || action === 'searchCompatible' || city || bloodType || status) {
            return donors.filter(d => {
                if (status && d.approvalStatus !== status) return false;
                if (!status && action === 'searchCompatible' && d.approvalStatus !== 'approved') return false;

                const isAvail = d.available !== undefined ? d.available : d.isAvailable;
                if (availability === 'available' && !isAvail) return false;
                if (availability === 'unavailable' && isAvail) return false;
                if (!availability && action === 'searchCompatible' && !isAvail) return false;

                if (city && city.trim() !== '') {
                    const targetCity = city.trim().toLowerCase();
                    if (!d.city || !d.city.toLowerCase().trim().includes(targetCity)) return false;
                }

                if (bloodType && bloodType.trim() !== '') {
                    if (action === 'searchCompatible') {
                        const recipientGroup = (bloodType.trim() + (rhFactor ? rhFactor.trim() : '')).toUpperCase();
                        const donorGroup = ((d.bloodType || '') + (d.rhFactor || '')).toUpperCase();
                        if (!isCompatibleBlood(donorGroup, recipientGroup)) return false;
                    } else {
                        if (d.bloodType !== bloodType.trim()) return false;
                    }
                }

                if (rhFactor && rhFactor.trim() !== '' && action !== 'searchCompatible') {
                    if (d.rhFactor !== rhFactor.trim()) return false;
                }

                return true;
            });
        }
        return donors;
    }

    if (servlet === 'HospitalServlet') {
        const hospitals = JSON.parse(localStorage.getItem('hemolink_hospitals') || '[]');
        const action = params.get('action') || 'list';
        const city = params.get('city');
        let res = hospitals;
        if (action === 'awaiting') res = hospitals.filter(h => h.approvalStatus === 'awaiting');
        else if (action === 'approved') res = hospitals.filter(h => h.approvalStatus === 'approved');

        if (city && city.trim() !== '') {
            const targetCity = city.trim().toLowerCase();
            res = res.filter(h => h.city && h.city.toLowerCase().trim().includes(targetCity));
        }
        return res;
    }

    if (servlet === 'BloodStockServlet') {
        const units = JSON.parse(localStorage.getItem('hemolink_blood_units') || '[]');
        const action = params.get('action');
        if (action === 'summary') {
            const stockMap = { 'A+': 0, 'A-': 0, 'B+': 0, 'B-': 0, 'AB+': 0, 'AB-': 0, 'O+': 0, 'O-': 0 };
            const warnings = {};
            units.forEach(u => {
                if (u.status === 'available') {
                    const group = u.bloodType + u.rhFactor;
                    stockMap[group] = (stockMap[group] || 0) + 1;
                }
            });
            for (let g in stockMap) warnings[g] = stockMap[g] < 3;
            return { stock: stockMap, warnings: warnings, lowStockThreshold: 3, totalAvailable: units.filter(u => u.status === 'available').length };
        }
        return units;
    }

    if (servlet === 'BloodRequestServlet') {
        const reqs = JSON.parse(localStorage.getItem('hemolink_requests') || '[]');
        const hospitalId = params.get('hospitalId');
        const city = params.get('city');
        let res = reqs;
        if (hospitalId) res = res.filter(r => r.hospitalId == hospitalId);
        if (city && city.trim() !== '') {
            const targetCity = city.trim().toLowerCase();
            res = res.filter(r => r.city && r.city.toLowerCase().trim().includes(targetCity));
        }
        return res;
    }

    if (servlet === 'DonationServlet') {
        const donations = JSON.parse(localStorage.getItem('hemolink_donations') || '[]');
        const donorId = params.get('donorId');
        const hospitalId = params.get('hospitalId');
        if (donorId) return donations.filter(d => d.donorId == donorId);
        if (hospitalId) return donations.filter(d => d.hospitalId == hospitalId);
        return donations;
    }

    if (servlet === 'NotificationServlet') {
        const notifs = JSON.parse(localStorage.getItem('hemolink_notifications') || '[]');
        const action = params.get('action');
        const user = getCurrentUser();
        const userId = user ? user.id : 1;

        let userNotifs = [];
        if (userId == 1) {
            userNotifs = notifs.filter(n => (n.userId == 1 || !n.userId) && (n.type === 'approval' || n.type === 'registration'));
        } else {
            userNotifs = notifs.filter(n => n.userId == userId);
        }

        if (action === 'unreadCount') {
            return { unreadCount: userNotifs.filter(n => !n.isRead).length };
        }
        return userNotifs;
    }

    if (servlet === 'ContactServlet') {
        const msgs = JSON.parse(localStorage.getItem('hemolink_contact_messages') || '[]');
        return msgs;
    }

    if (servlet === 'DashboardServlet') {
        const action = params.get('action') || 'admin';
        const donors = JSON.parse(localStorage.getItem('hemolink_donors') || '[]');
        const hospitals = JSON.parse(localStorage.getItem('hemolink_hospitals') || '[]');
        const units = JSON.parse(localStorage.getItem('hemolink_blood_units') || '[]');
        const reqs = JSON.parse(localStorage.getItem('hemolink_requests') || '[]');
        const donations = JSON.parse(localStorage.getItem('hemolink_donations') || '[]');

        if (action === 'donor') {
            const currentDonor = donors.find(d => currentUser && d.userId === currentUser.id);
            const donorType = currentDonor ? currentDonor.bloodType + currentDonor.rhFactor : '';
            const matchedReqs = reqs.filter(r => {
                if (!currentDonor) return false;
                if (r.status !== 'matching' && r.status !== 'pending' && r.status !== 'accepted' && r.status !== 'fulfilled' && r.status !== 'completed') return false;
                const requestType = r.bloodType + r.rhFactor;
                if (!isCompatibleBlood(donorType, requestType)) return false;

                const isAssignedToMe = (r.assignedDonorId && parseInt(r.assignedDonorId) === currentDonor.id);
                if (isAssignedToMe) return true;

                if (r.matchedDonorIds && r.matchedDonorIds.trim() !== '') {
                    const matchedIds = r.matchedDonorIds.split(',').map(id => id.trim()).filter(Boolean);
                    return matchedIds.includes(String(currentDonor.id));
                }
                return true;
            });
            return {
                donor: currentDonor,
                isEligible: currentDonor ? currentDonor.isAvailable : true,
                eligibilityReason: 'Eligible to donate',
                cooldownDaysRemaining: 0,
                nextEligibleDate: '2026-07-22',
                totalDonations: currentDonor ? currentDonor.totalDonations : 0,
                matchedRequests: matchedReqs,
                history: donations.filter(d => currentDonor && d.donorId === currentDonor.id)
            };
        }

        if (action === 'hospital') {
            const currentHosp = hospitals.find(h => currentUser && h.userId === currentUser.id) || hospitals[0];
            const hospReqs = reqs.filter(r => currentHosp && r.hospitalId === currentHosp.id);
            return {
                hospital: currentHosp,
                totalRequests: hospReqs.length,
                activeRequests: hospReqs.filter(r => r.status === 'pending' || r.status === 'matching' || r.status === 'accepted' || r.status === 'fulfilled').length,
                donationsCount: donations.filter(d => currentHosp && d.hospitalId === currentHosp.id).length,
                availableUnits: units.filter(u => u.status === 'available').length,
                recentRequests: hospReqs,
                recentDonations: donations.filter(d => currentHosp && d.hospitalId === currentHosp.id)
            };
        }

        // Admin dashboard stats
        const stockMap = { 'A+': 0, 'A-': 0, 'B+': 0, 'B-': 0, 'AB+': 0, 'AB-': 0, 'O+': 0, 'O-': 0 };
        units.forEach(u => { if (u.status === 'available') stockMap[u.bloodType + u.rhFactor] = (stockMap[u.bloodType + u.rhFactor] || 0) + 1; });

        const donorDist = { 'A+': 0, 'A-': 0, 'B+': 0, 'B-': 0, 'AB+': 0, 'AB-': 0, 'O+': 0, 'O-': 0 };
        donors.forEach(d => { if (d.approvalStatus === 'approved') donorDist[d.bloodType + d.rhFactor] = (donorDist[d.bloodType + d.rhFactor] || 0) + 1; });

        return {
            approvedDonors: donors.filter(d => d.approvalStatus === 'approved').length,
            bloodUnits: units.filter(u => u.status === 'available').length,
            activeRequests: reqs.filter(r => r.status === 'pending' || r.status === 'matching').length,
            criticalRequests: reqs.filter(r => r.urgency === 'critical' && (r.status === 'pending' || r.status === 'matching')).length,
            hospitals: hospitals.filter(h => h.approvalStatus === 'approved').length,
            pendingApprovals: donors.filter(d => d.approvalStatus === 'awaiting').length + hospitals.filter(h => h.approvalStatus === 'awaiting').length,
            totalDonations: donations.length,
            bloodStock: stockMap,
            donorDistribution: donorDist
        };
    }

    if (servlet === 'SettingsServlet') {
        return [
            { id: 1, settingKey: 'system_name', settingValue: 'HemoLink' },
            { id: 2, settingKey: 'cooldown_days', settingValue: '56' },
            { id: 3, settingKey: 'min_weight', settingValue: '50' },
            { id: 4, settingKey: 'min_age', settingValue: '18' },
            { id: 5, settingKey: 'max_age', settingValue: '65' },
            { id: 6, settingKey: 'unit_volume', settingValue: '450' },
            { id: 7, settingKey: 'expiry_days', settingValue: '42' },
            { id: 8, settingKey: 'low_stock_threshold', settingValue: '3' },
            { id: 9, settingKey: 'emergency_sms', settingValue: 'true' },
            { id: 10, settingKey: 'auto_match', settingValue: 'false' }
        ];
    }

    return [];
}

// Mock POST Router
function handleMockPost(url, data) {
    const servlet = url.split('?')[0].split('/').pop();
    const action = data.action || '';
    const currentUser = getCurrentUser();

    if (servlet === 'LoginServlet') {
        const username = (data.username || '').trim();
        const password = (data.password || '').trim();

        const users = JSON.parse(localStorage.getItem('hemolink_users') || '[]');
        const user = users.find(u => u.username === username && u.password === password);

        if (user) {
            if (user.role === 'donor') {
                const donors = JSON.parse(localStorage.getItem('hemolink_donors') || '[]');
                const donor = donors.find(d => d.userId === user.id);
                if (!donor) {
                    return { success: false, message: 'Donor profile not found. Please contact admin.' };
                }
                const st = (donor.approvalStatus || 'awaiting').trim().toLowerCase();
                if (st === 'rejected') {
                    return { success: false, message: 'Your account registration has been rejected.' };
                }
                if (st !== 'approved') {
                    return { success: false, message: 'Your account is waiting for admin approval.' };
                }
            } else if (user.role === 'hospital') {
                const hospitals = JSON.parse(localStorage.getItem('hemolink_hospitals') || '[]');
                const hosp = hospitals.find(h => h.userId === user.id);
                if (!hosp) {
                    return { success: false, message: 'Hospital profile not found. Please contact admin.' };
                }
                const st = (hosp.approvalStatus || 'awaiting').trim().toLowerCase();
                if (st === 'rejected') {
                    return { success: false, message: 'Your account registration has been rejected.' };
                }
                if (st !== 'approved') {
                    return { success: false, message: 'Your account is waiting for admin approval.' };
                }
            }

            sessionStorage.setItem('hemolink_session_user', JSON.stringify(user));
            let redirect = 'admin/dashboard.html';
            if (user.role === 'donor') redirect = 'donor/dashboard.html';
            if (user.role === 'hospital') redirect = 'hospital/dashboard.html';
            return { success: true, message: 'Login successful.', role: user.role, redirect: redirect };
        } else {
            return { success: false, message: 'Invalid username or password.' };
        }
    }

    if (servlet === 'RegisterServlet') {
        const role = data.role || 'donor';
        const users = JSON.parse(localStorage.getItem('hemolink_users') || '[]');
        const newUserId = users.length + 1;

        const userObj = {
            id: newUserId,
            username: data.username,
            password: data.password,
            role: role,
            name: data.name,
            email: data.email,
            phone: data.phone,
            createdAt: new Date().toISOString().substring(0, 10)
        };
        users.push(userObj);
        localStorage.setItem('hemolink_users', JSON.stringify(users));

        if (role === 'donor') {
            const donors = JSON.parse(localStorage.getItem('hemolink_donors') || '[]');
            donors.push({
                id: donors.length + 1,
                userId: newUserId,
                name: data.name,
                bloodType: data.bloodType || 'A',
                rhFactor: data.rhFactor || '+',
                age: parseInt(data.age || 25),
                gender: data.gender || 'Male',
                city: data.city || 'Colombo',
                address: '',
                weight: parseInt(data.weight || 60),
                phone: data.phone,
                medicalConditions: 'None',
                lastDonationDate: null,
                isAvailable: true,
                totalDonations: 0,
                approvalStatus: 'awaiting'
            });
            localStorage.setItem('hemolink_donors', JSON.stringify(donors));

            const notifications = JSON.parse(localStorage.getItem('hemolink_notifications') || '[]');
            notifications.unshift({
                id: notifications.length + 1,
                userId: 1,
                title: 'New Donor Registration',
                message: data.name + ' (' + (data.bloodType || 'A') + (data.rhFactor || '+') + ') registered as a donor and is awaiting approval.',
                type: 'approval',
                isRead: false,
                createdAt: new Date().toISOString()
            });
            localStorage.setItem('hemolink_notifications', JSON.stringify(notifications));
        } else {
            const hospitals = JSON.parse(localStorage.getItem('hemolink_hospitals') || '[]');
            hospitals.push({
                id: hospitals.length + 1,
                userId: newUserId,
                name: data.name,
                city: data.city,
                address: '',
                license: data.license || 'SL-2024-XXX',
                type: 'Private',
                approvalStatus: 'awaiting'
            });
            localStorage.setItem('hemolink_hospitals', JSON.stringify(hospitals));

            const notifications = JSON.parse(localStorage.getItem('hemolink_notifications') || '[]');
            notifications.unshift({
                id: notifications.length + 1,
                userId: 1,
                title: 'New Hospital Registration',
                message: data.name + ' registered as a hospital and is awaiting approval.',
                type: 'approval',
                isRead: false,
                createdAt: new Date().toISOString()
            });
            localStorage.setItem('hemolink_notifications', JSON.stringify(notifications));
        }

        return { success: true, message: 'Registration submitted! Awaiting admin approval.' };
    }

    if (servlet === 'DonorServlet') {
        const donors = JSON.parse(localStorage.getItem('hemolink_donors') || '[]');
        if (action === 'approve' || action === 'reject') {
            const d = donors.find(x => x.id == data.id);
            if (d) d.approvalStatus = action === 'approve' ? 'approved' : 'rejected';
            localStorage.setItem('hemolink_donors', JSON.stringify(donors));
            return { success: true, message: 'Donor status updated to ' + action };
        }
        if (action === 'toggleAvailability') {
            const currentSession = JSON.parse(sessionStorage.getItem('hemolink_session_user') || 'null');
            const d = donors.find(x => currentSession && x.userId === currentSession.id);
            if (d) {
                d.isAvailable = !d.isAvailable;
                localStorage.setItem('hemolink_donors', JSON.stringify(donors));
                return { success: true, available: d.isAvailable, message: 'Availability updated' };
            }
        }
        if (action === 'update') {
            const d = donors.find(x => x.id == data.id);
            if (d) {
                d.name = data.name;
                d.age = parseInt(data.age);
                d.gender = data.gender;
                d.weight = parseInt(data.weight);
                d.phone = data.phone;
                d.city = data.city;
                d.address = data.address;
                d.medicalConditions = data.medicalConditions;
                localStorage.setItem('hemolink_donors', JSON.stringify(donors));
                return { success: true, message: 'Profile updated' };
            }
        }
    }

    if (servlet === 'HospitalServlet') {
        const hospitals = JSON.parse(localStorage.getItem('hemolink_hospitals') || '[]');
        if (action === 'approve' || action === 'reject') {
            const h = hospitals.find(x => x.id == data.id);
            if (h) h.approvalStatus = action === 'approve' ? 'approved' : 'rejected';
            localStorage.setItem('hemolink_hospitals', JSON.stringify(hospitals));
            return { success: true, message: 'Hospital status updated' };
        }
    }

    if (servlet === 'DonationServlet') {
        const donations = JSON.parse(localStorage.getItem('hemolink_donations') || '[]');
        const donors = JSON.parse(localStorage.getItem('hemolink_donors') || '[]');
        const units = JSON.parse(localStorage.getItem('hemolink_blood_units') || '[]');

        const donor = donors.find(x => x.id == data.donorId);
        if (donor) {
            donations.push({
                id: donations.length + 1,
                donorId: donor.id,
                donorName: donor.name,
                bloodType: donor.bloodType,
                rhFactor: donor.rhFactor,
                volumeMl: parseInt(data.volumeMl || 450),
                donatedAt: new Date().toISOString().replace('T', ' ').substring(0, 19),
                hospitalName: data.hospitalName || 'Central Blood Bank',
                hospitalId: 1,
                notes: data.notes || 'Routine donation'
            });
            donor.lastDonationDate = new Date().toISOString().substring(0, 10);
            donor.totalDonations = (donor.totalDonations || 0) + 1;

            units.push({
                id: units.length + 1,
                bloodType: donor.bloodType,
                rhFactor: donor.rhFactor,
                volumeMl: parseInt(data.volumeMl || 450),
                collectedDate: new Date().toISOString().substring(0, 10),
                expiresDate: new Date(Date.now() + 42 * 86400000).toISOString().substring(0, 10),
                donorId: donor.id,
                donorName: donor.name,
                status: 'available'
            });

            localStorage.setItem('hemolink_donations', JSON.stringify(donations));
            localStorage.setItem('hemolink_donors', JSON.stringify(donors));
            localStorage.setItem('hemolink_blood_units', JSON.stringify(units));
            return { success: true, message: 'Donation recorded successfully! Blood stock increased.' };
        }
    }

    if (servlet === 'BloodStockServlet') {
        const units = JSON.parse(localStorage.getItem('hemolink_blood_units') || '[]');
        const count = parseInt(data.unitsCount || 1);
        for (let i = 0; i < count; i++) {
            units.push({
                id: units.length + 1,
                bloodType: data.bloodType,
                rhFactor: data.rhFactor,
                volumeMl: parseInt(data.volumeMl || 450),
                collectedDate: new Date().toISOString().substring(0, 10),
                expiresDate: new Date(Date.now() + 42 * 86400000).toISOString().substring(0, 10),
                donorId: null,
                donorName: data.donorName || 'Manual Addition',
                status: 'available'
            });
        }
        localStorage.setItem('hemolink_blood_units', JSON.stringify(units));
        return { success: true, message: count + ' blood unit(s) added successfully.' };
    }

    if (servlet === 'BloodRequestServlet') {
        const reqs = JSON.parse(localStorage.getItem('hemolink_requests') || '[]');
        const donors = JSON.parse(localStorage.getItem('hemolink_donors') || '[]');
        const hospitals = JSON.parse(localStorage.getItem('hemolink_hospitals') || '[]');
        const notifications = JSON.parse(localStorage.getItem('hemolink_notifications') || '[]');
        const donations = JSON.parse(localStorage.getItem('hemolink_donations') || '[]');

        if (action === 'add') {
            const hospId = parseInt(data.hospitalId || 1);
            const hosp = hospitals.find(h => h.id === hospId || (currentUser && h.userId === currentUser.id));
            if (hosp && hosp.approvalStatus !== 'approved') {
                return { success: false, message: 'Only registered and approved hospitals can create blood requests.' };
            }

            const reqGroup = (data.bloodType || 'O') + (data.rhFactor || '+');
            const reqCity = (data.city || '').trim().toLowerCase();

            // Perform automatic donor matching
            const matchedDonors = donors.filter(d => {
                if (d.approvalStatus !== 'approved') return false;
                const isAvail = (d.available !== undefined ? d.available : d.isAvailable);
                if (!isAvail) return false;
                const dGroup = (d.bloodType || '') + (d.rhFactor || '');
                return isCompatibleBlood(dGroup, reqGroup);
            });

            // Sort matched donors: city match first
            matchedDonors.sort((a, b) => {
                const aCity = (a.city || '').toLowerCase() === reqCity ? 1 : 0;
                const bCity = (b.city || '').toLowerCase() === reqCity ? 1 : 0;
                return bCity - aCity;
            });

            const matchedIds = matchedDonors.map(m => m.id).join(',');

            const reqId = reqs.length + 1;
            const newReq = {
                id: reqId,
                patientName: data.patientName,
                bloodType: data.bloodType,
                rhFactor: data.rhFactor,
                unitsNeeded: parseInt(data.unitsNeeded || 1),
                urgency: data.urgency || 'routine',
                hospitalId: parseInt(data.hospitalId || 1),
                hospitalName: data.hospitalName || 'Hospital',
                city: data.city || '',
                contactPerson: data.contactPerson || '',
                phone: data.phone || '',
                notes: data.notes || '',
                status: 'matching',
                matchedDonorIds: matchedIds,
                acceptedDonorIds: '',
                assignedDonorId: 0,
                acceptedUnits: 0,
                donorResponses: '',
                hospitalNote: '',
                referenceId: 'REQ-' + Date.now(),
                createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
            };
            reqs.unshift(newReq);
            localStorage.setItem('hemolink_requests', JSON.stringify(reqs));

            // Notify matched donors DIRECTLY
            matchedDonors.forEach(d => {
                if (d.userId) {
                    notifications.unshift({
                        id: notifications.length + 1,
                        userId: d.userId,
                        title: 'New Blood Request',
                        message: 'A request for ' + newReq.unitsNeeded + ' unit(s) of ' + newReq.bloodType + newReq.rhFactor + ' blood at ' + newReq.hospitalName + ' (' + newReq.city + ') is available. Please respond if you can donate.',
                        type: 'request',
                        isRead: false,
                        createdAt: new Date().toISOString()
                    });
                }
            });

            localStorage.setItem('hemolink_notifications', JSON.stringify(notifications));

            return {
                success: true,
                requestId: reqId,
                matchedCount: matchedDonors.length,
                message: 'Blood request submitted successfully and sent directly to ' + matchedDonors.length + ' matching donor(s).'
            };
        }

        if (action === 'fulfill') {
            const reqId = parseInt(data.id);
            const req = reqs.find(x => x.id === reqId);

            if (!req) return { success: false, message: 'Request not found.' };

            const requestedGroup = (req.bloodType || 'O') + (req.rhFactor || '+');
            const reqCity = (req.city || '').trim().toLowerCase();

            // Find compatible, available, approved donors
            const matchedDonors = donors.filter(d => {
                if (d.approvalStatus !== 'approved') return false;
                const isAvail = d.available !== undefined ? d.available : d.isAvailable;
                if (!isAvail) return false;
                const donorGroup = (d.bloodType || '') + (d.rhFactor || '');
                return isCompatibleBlood(donorGroup, requestedGroup);
            });

            // Sort matched donors: city match first
            matchedDonors.sort((a, b) => {
                const aCity = (a.city || '').toLowerCase() === reqCity ? 1 : 0;
                const bCity = (b.city || '').toLowerCase() === reqCity ? 1 : 0;
                return bCity - aCity;
            });

            const matchedIds = matchedDonors.map(m => m.id).join(',');
            req.matchedDonorIds = matchedIds;
            req.status = 'matching';

            localStorage.setItem('hemolink_requests', JSON.stringify(reqs));

            // Notify matched donors
            matchedDonors.forEach(d => {
                if (d.userId) {
                    notifications.unshift({
                        id: notifications.length + 1,
                        userId: d.userId,
                        title: 'New Blood Request',
                        message: 'A request for ' + req.unitsNeeded + ' unit(s) of ' + req.bloodType + req.rhFactor + ' blood at ' + req.hospitalName + ' (' + req.city + ') is available. Please respond if you can donate.',
                        type: 'request',
                        isRead: false,
                        createdAt: new Date().toISOString()
                    });
                }
            });

            // Notify hospital
            const currentHosp = hospitals.find(h => h.id === req.hospitalId);
            if (currentHosp && currentHosp.userId) {
                notifications.unshift({
                    id: notifications.length + 1,
                    userId: currentHosp.userId,
                    title: 'Request Reviewed & Fulfilled',
                    message: 'Your blood request #' + reqId + ' has been reviewed & fulfilled by Admin. ' + matchedDonors.length + ' compatible donor(s) were notified.',
                    type: 'success',
                    isRead: false,
                    createdAt: new Date().toISOString()
                });
            }

            localStorage.setItem('hemolink_notifications', JSON.stringify(notifications));

            return {
                success: true,
                matches: matchedDonors,
                message: 'Blood request fulfilled by Admin. Matched donors have been notified.'
            };
        }

        if (action === 'respond') {
            const requestId = parseInt(data.requestId);
            const donorId = parseInt(data.donorId);
            const responseStr = (data.response || '').toLowerCase();
            const req = reqs.find(x => x.id === requestId);

            if (!req) return { success: false, message: 'Request not found.' };

            const d = donors.find(x => x.id === donorId);
            const h = hospitals.find(x => x.id === req.hospitalId);

            if (responseStr === 'accept') {
                // FIRST DONOR WINS: check if already assigned to another donor
                if (req.assignedDonorId && req.assignedDonorId !== 0 && req.assignedDonorId !== donorId) {
                    return {
                        success: false,
                        isLocked: true,
                        message: 'This request has already been accepted by another donor.'
                    };
                }

                req.assignedDonorId = donorId;
                req.acceptedUnits = 1;
                req.acceptedDonorIds = String(donorId);
                req.status = 'fulfilled';

                // Parse existing responses map
                let respMap = {};
                if (req.donorResponses) {
                    req.donorResponses.split(';').forEach(p => {
                        const kv = p.trim().split(':');
                        if (kv.length === 2) respMap[kv[0].replace('donor_', '').trim()] = kv[1].trim().toLowerCase();
                    });
                }
                respMap[donorId] = 'accept';
                req.donorResponses = Object.keys(respMap).map(k => 'donor_' + k + ':' + respMap[k]).join(';');

                localStorage.setItem('hemolink_requests', JSON.stringify(reqs));

                if (h && h.userId) {
                    notifications.unshift({
                        id: notifications.length + 1,
                        userId: h.userId,
                        title: 'Donor Accepted Request',
                        message: (d ? d.name : 'A donor') + ' has ACCEPTED blood request #' + requestId + '. Contact details are now available.',
                        type: 'success',
                        isRead: false,
                        createdAt: new Date().toISOString()
                    });
                }
                if (d && d.userId) {
                    notifications.unshift({
                        id: notifications.length + 1,
                        userId: d.userId,
                        title: 'Request Accepted',
                        message: 'You accepted request #' + requestId + ' for ' + req.hospitalName + '. Thank you for donating!',
                        type: 'success',
                        isRead: false,
                        createdAt: new Date().toISOString()
                    });
                }

                localStorage.setItem('hemolink_notifications', JSON.stringify(notifications));
                return { success: true, isAssigned: true, message: 'Request accepted and assigned to you!' };
            } else {
                if (h && h.userId) {
                    notifications.unshift({
                        id: notifications.length + 1,
                        userId: h.userId,
                        title: 'Donor Declined Request',
                        message: (d ? d.name : 'A donor') + ' has declined request #' + requestId + '.',
                        type: 'alert',
                        isRead: false,
                        createdAt: new Date().toISOString()
                    });
                }

                localStorage.setItem('hemolink_notifications', JSON.stringify(notifications));
                return { success: true, message: 'Response recorded (DECLINED).' };
            }
        }

        if (action === 'complete') {
            const reqId = parseInt(data.id);
            const hospitalNote = (data.hospitalNote || 'Donation successfully completed.').trim();
            const req = reqs.find(x => x.id === reqId);

            if (!req) return { success: false, message: 'Request not found.' };

            let assignedDonorId = req.assignedDonorId || 0;
            if (assignedDonorId === 0 && req.acceptedDonorIds) {
                const parts = req.acceptedDonorIds.split(',').map(x => parseInt(x.trim())).filter(Boolean);
                if (parts.length > 0) assignedDonorId = parts[0];
            }

            if (assignedDonorId === 0) {
                return { success: false, message: 'Cannot complete donation: No donor has accepted this request yet.' };
            }

            req.status = 'completed';
            req.hospitalNote = hospitalNote;

            const nowTimeStr = new Date().toISOString().replace('T', ' ').substring(0, 19);

            const d = donors.find(x => x.id === assignedDonorId);
            if (d) {
                d.totalDonations = (d.totalDonations || 0) + 1;
                d.lastDonationDate = new Date().toISOString().substring(0, 10);

                donations.unshift({
                    id: donations.length + 1,
                    requestId: req.id,
                    donorId: d.id,
                    donorName: d.name,
                    bloodType: d.bloodType,
                    rhFactor: d.rhFactor,
                    volumeMl: 450,
                    requestDate: req.createdAt,
                    donatedAt: nowTimeStr,
                    hospitalName: req.hospitalName,
                    hospitalId: req.hospitalId,
                    status: 'completed',
                    hospitalNote: hospitalNote,
                    referenceId: 'DON-' + Date.now() + '-' + d.id,
                    notes: hospitalNote
                });

                if (d.userId) {
                    notifications.unshift({
                        id: notifications.length + 1,
                        userId: d.userId,
                        title: 'Donation Completed',
                        message: 'Your donation for request #' + req.id + ' was marked completed. Note from ' + req.hospitalName + ': "' + hospitalNote + '"',
                        type: 'success',
                        isRead: false,
                        createdAt: new Date().toISOString()
                    });
                }
            }

            const h = hospitals.find(x => x.id === req.hospitalId);
            if (h && h.userId) {
                notifications.unshift({
                    id: notifications.length + 1,
                    userId: h.userId,
                    title: 'Donation Completed',
                    message: 'Blood request #' + req.id + ' marked as completed with note: "' + hospitalNote + '".',
                    type: 'success',
                    isRead: false,
                    createdAt: new Date().toISOString()
                });
            }

            localStorage.setItem('hemolink_requests', JSON.stringify(reqs));
            localStorage.setItem('hemolink_donors', JSON.stringify(donors));
            localStorage.setItem('hemolink_donations', JSON.stringify(donations));
            localStorage.setItem('hemolink_notifications', JSON.stringify(notifications));

            return { success: true, message: 'Donation completed and history updated successfully.' };
        }

        if (action === 'cancel') {
            const req = reqs.find(x => x.id == data.id);
            if (req) req.status = 'cancelled';
            localStorage.setItem('hemolink_requests', JSON.stringify(reqs));
            return { success: true, message: 'Request cancelled.' };
        }

        if (action === 'emergencyAlert') {
            const donorId = data.donorId;
            const donors = JSON.parse(localStorage.getItem('hemolink_donors') || '[]');
            const donor = donors.find(d => d.id == donorId);
            if (donor && donor.userId) {
                const notifications = JSON.parse(localStorage.getItem('hemolink_notifications') || '[]');
                notifications.unshift({
                    id: notifications.length + 1,
                    userId: donor.userId,
                    title: 'URGENT: Emergency Blood Needed!',
                    message: (data.hospitalName || 'Hospital') + ' requires urgent ' + (data.bloodGroup || '') + ' blood group donation! Please contact the hospital immediately.',
                    type: 'alert',
                    isRead: false,
                    createdAt: new Date().toISOString()
                });
                localStorage.setItem('hemolink_notifications', JSON.stringify(notifications));
            }
            return { success: true, message: 'Emergency alert dispatched to donor' };
        }
    }

    if (servlet === 'MatchingServlet') {
        const requestId = data.requestId;
        const reqs = JSON.parse(localStorage.getItem('hemolink_requests') || '[]');
        const req = reqs.find(x => x.id == requestId);
        const donors = JSON.parse(localStorage.getItem('hemolink_donors') || '[]');

        if (req) req.status = 'matching';
        localStorage.setItem('hemolink_requests', JSON.stringify(reqs));

        const matches = donors.filter(d => d.approvalStatus === 'approved' && d.isAvailable).map(d => {
            let score = 50;
            if (d.bloodType + d.rhFactor === req.bloodType + req.rhFactor) score += 35;
            if (d.city && req.city && d.city.toLowerCase() === req.city.toLowerCase()) score += 15;
            return { donor: d, score: Math.min(100, score) };
        });

        matches.sort((a,b) => b.score - a.score);
        return matches;
    }

    if (servlet === 'NotificationServlet') {
        const notifs = JSON.parse(localStorage.getItem('hemolink_notifications') || '[]');
        if (action === 'markRead') {
            const n = notifs.find(x => x.id == data.id);
            if (n) n.isRead = true;
            localStorage.setItem('hemolink_notifications', JSON.stringify(notifs));
            return { success: true, message: 'Notification marked as read.' };
        }
        if (action === 'markAllRead') {
            notifs.forEach(n => n.isRead = true);
            localStorage.setItem('hemolink_notifications', JSON.stringify(notifs));
            return { success: true, message: 'All notifications marked as read.' };
        }
        return { success: true, message: 'Notifications updated' };
    }

    if (servlet === 'ContactServlet') {
        const msgs = JSON.parse(localStorage.getItem('hemolink_contact_messages') || '[]');
        if (action === 'markRead') {
            const m = msgs.find(x => x.id == data.id);
            if (m) m.status = 'read';
            localStorage.setItem('hemolink_contact_messages', JSON.stringify(msgs));
            return { success: true, message: 'Message marked as read.' };
        }

        const newMsg = {
            id: msgs.length + 1,
            name: (data.name || '').trim(),
            email: (data.email || '').trim(),
            message: (data.message || '').trim(),
            status: 'unread',
            createdAt: new Date().toISOString().replace('T', ' ').substring(0, 19)
        };
        msgs.unshift(newMsg);
        localStorage.setItem('hemolink_contact_messages', JSON.stringify(msgs));

        // Create admin notification
        const notifications = JSON.parse(localStorage.getItem('hemolink_notifications') || '[]');
        notifications.unshift({
            id: notifications.length + 1,
            userId: 1,
            title: 'New Contact Message',
            message: 'From: ' + newMsg.name + ' (' + newMsg.email + ') - ' + newMsg.message,
            type: 'contact',
            isRead: false,
            createdAt: new Date().toISOString()
        });
        localStorage.setItem('hemolink_notifications', JSON.stringify(notifications));

        return { success: true, message: 'Your message has been sent successfully.' };
    }

    if (servlet === 'SettingsServlet') {
        return { success: true, message: 'Settings saved successfully.' };
    }

    return { success: true, message: 'Operation completed' };
}

// Toast Notifications
function showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    
    let icon = 'fa-circle-info';
    if (type === 'success') icon = 'fa-circle-check';
    if (type === 'error') icon = 'fa-triangle-exclamation';

    toast.innerHTML = `<i class="fa-solid ${icon}"></i> <span>${message}</span>`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

// Modal Handlers
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.add('active');
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.remove('active');
}

// Password Visibility Toggle Handler
function togglePasswordVisibility(inputId, btnElement) {
    const input = document.getElementById(inputId);
    if (!input) return;

    const icon = btnElement ? (btnElement.querySelector('i') || btnElement) : null;

    if (input.type === 'password') {
        input.type = 'text';
        if (icon) {
            icon.classList.remove('fa-eye');
            icon.classList.add('fa-eye-slash');
        }
        if (btnElement && btnElement.setAttribute) {
            btnElement.setAttribute('title', 'Hide password');
        }
    } else {
        input.type = 'password';
        if (icon) {
            icon.classList.remove('fa-eye-slash');
            icon.classList.add('fa-eye');
        }
        if (btnElement && btnElement.setAttribute) {
            btnElement.setAttribute('title', 'Show password');
        }
    }
}

// Notification Badge Update
async function updateNotifBadge() {
    try {
        const data = await apiGet('NotificationServlet?action=unreadCount');
        const badge = document.getElementById('notif-badge-count');
        if (badge && data && typeof data.unreadCount !== 'undefined') {
            badge.innerText = data.unreadCount;
            badge.style.display = data.unreadCount > 0 ? 'inline-block' : 'none';
        }
    } catch (e) {}
}

// Global Init on DOM Load
document.addEventListener('DOMContentLoaded', async () => {
    updateNotifBadge();
    await requireAuth();

    document.querySelectorAll('.btn-logout-icon').forEach(link => {
        link.addEventListener('click', event => {
            event.preventDefault();
            logoutUser();
        });
    });

    // Highlight Active Sidebar Item
    const currentPath = window.location.pathname;
    const links = document.querySelectorAll('.menu-item');
    links.forEach(link => {
        if (currentPath.includes(link.getAttribute('href'))) {
            link.classList.add('active');
        }
    });
});
