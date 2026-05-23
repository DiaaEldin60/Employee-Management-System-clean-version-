// Leave Requests-specific JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize sidebar toggle (using common function)
    initSidebarToggle();
    
    // Fetch leave requests on page load
    fetchMyLeaveRequests();
    if (document.querySelector('[sec\\:authorize*="ADMIN"], [sec\\:authorize*="MANAGER"]')) {
        fetchPendingLeaveRequests();
    }
});

// Leave management functions
function submitLeaveRequest() {
    const leaveType = document.getElementById('leaveType').value;
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const reason = document.getElementById('reason').value;

    if (!leaveType || !startDate || !endDate) {
        alert('Please fill in all required fields');
        return;
    }

    showLoading();
    fetch('/api/v1/leave/requests', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            leaveType: leaveType,
            startDate: startDate,
            endDate: endDate,
            reason: reason
        })
    })
    .then(response => {
        if (response.ok) {
            hideLoading();
            alert('Leave request submitted successfully');
            bootstrap.Modal.getInstance(document.getElementById('leaveRequestModal')).hide();
            document.getElementById('leaveRequestForm').reset();
            fetchMyLeaveRequests();
        } else {
            hideLoading();
            alert('Failed to submit leave request');
        }
    })
    .catch(error => {
        console.error('Submit error:', error);
        hideLoading();
        alert('Failed to submit leave request');
    });
}

function fetchPendingLeaveRequests() {
    fetch('/api/v1/leave/requests/pending')
        .then(response => response.json())
        .then(requests => {
            const container = document.getElementById('pendingLeaveRequestsList');
            if (requests.length === 0) {
                container.innerHTML = '<p class="text-muted">No pending leave requests.</p>';
                return;
            }

            let html = '<div class="table-responsive"><table class="table table-sm"><thead><tr><th>Employee</th><th>Type</th><th>Dates</th><th>Days</th><th>Reason</th><th>Actions</th></tr></thead><tbody>';
            requests.forEach(request => {
                html += `
                    <tr>
                        <td>${request.employee.firstName} ${request.employee.lastName}</td>
                        <td><span class="badge ${getLeaveTypeBadgeClass(request.leaveType)}">${request.leaveType}</span></td>
                        <td>${request.startDate} to ${request.endDate}</td>
                        <td>${request.daysCount}</td>
                        <td>${request.reason || '-'}</td>
                        <td>
                            <button class="btn btn-sm btn-success" onclick="approveLeaveRequest(${request.id})">
                                <i class="fas fa-check"></i>
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="rejectLeaveRequest(${request.id})">
                                <i class="fas fa-times"></i>
                            </button>
                        </td>
                    </tr>
                `;
            });
            html += '</tbody></table></div>';
            container.innerHTML = html;
            document.getElementById('pendingLeaveRequestsSection').style.display = 'block';
        })
        .catch(error => {
            console.error('Error fetching pending leave requests:', error);
            document.getElementById('pendingLeaveRequestsList').innerHTML = '<p class="text-danger">Failed to load pending requests.</p>';
        });
}

function fetchMyLeaveRequests() {
    fetch('/api/v1/leave/requests/my')
        .then(response => response.json())
        .then(requests => {
            const container = document.getElementById('myLeaveRequestsList');
            if (requests.length === 0) {
                container.innerHTML = '<p class="text-muted">No leave requests found.</p>';
                return;
            }

            let html = '<div class="table-responsive"><table class="table table-sm"><thead><tr><th>Type</th><th>Dates</th><th>Days</th><th>Reason</th><th>Status</th></tr></thead><tbody>';
            requests.forEach(request => {
                html += `
                    <tr>
                        <td><span class="badge ${getLeaveTypeBadgeClass(request.leaveType)}">${request.leaveType}</span></td>
                        <td>${request.startDate} to ${request.endDate}</td>
                        <td>${request.daysCount}</td>
                        <td>${request.reason || '-'}</td>
                        <td><span class="badge ${getStatusBadgeClass(request.status)}">${request.status}</span></td>
                    </tr>
                `;
            });
            html += '</tbody></table></div>';
            container.innerHTML = html;
        })
        .catch(error => {
            console.error('Error fetching my leave requests:', error);
            document.getElementById('myLeaveRequestsList').innerHTML = '<p class="text-danger">Failed to load your leave requests.</p>';
        });
}

function getLeaveTypeBadgeClass(type) {
    switch(type) {
        case 'ANNUAL': return 'bg-warning text-dark';
        case 'SICK': return 'bg-danger';
        case 'EMERGENCY': return 'bg-info';
        default: return 'bg-secondary';
    }
}

function getStatusBadgeClass(status) {
    switch(status) {
        case 'PENDING': return 'bg-warning text-dark';
        case 'APPROVED': return 'bg-success';
        case 'REJECTED': return 'bg-danger';
        default: return 'bg-secondary';
    }
}

function approveLeaveRequest(requestId) {
    if (confirm('Are you sure you want to approve this leave request?')) {
        showLoading();
        fetch(`/api/v1/leave/requests/${requestId}/approve`, {
            method: 'POST'
        })
        .then(response => {
            if (response.ok) {
                hideLoading();
                alert('Leave request approved successfully');
                fetchPendingLeaveRequests();
            } else {
                hideLoading();
                alert('Failed to approve leave request');
            }
        })
        .catch(error => {
            console.error('Approve error:', error);
            hideLoading();
            alert('Failed to approve leave request');
        });
    }
}

function rejectLeaveRequest(requestId) {
    const rejectionReason = prompt('Please provide a reason for rejection:');
    if (rejectionReason === null) return;

    showLoading();
    fetch(`/api/v1/leave/requests/${requestId}/reject?rejectionReason=${encodeURIComponent(rejectionReason)}`, {
        method: 'POST'
    })
    .then(response => {
        if (response.ok) {
            hideLoading();
            alert('Leave request rejected successfully');
            fetchPendingLeaveRequests();
        } else {
            hideLoading();
            alert('Failed to reject leave request');
        }
    })
    .catch(error => {
        console.error('Reject error:', error);
        hideLoading();
        alert('Failed to reject leave request');
    });
}
