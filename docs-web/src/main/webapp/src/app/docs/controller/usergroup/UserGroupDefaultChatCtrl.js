'use strict';

angular.module('docs').controller('UserGroupDefaultChatCtrl',
  function ($scope, $rootScope, $timeout, $interval, Restangular) {
    // Scope Variable Definitions
    $scope.availableUsers = [];
    $scope.selectedUserToChatWith = null;
    $scope.messages = {};
    $scope.chatInput = { text: '' };
    $scope.currentUser = $rootScope.userInfo;
    $scope.loggedInUsername = null;
    $scope.isLoadingHistory = false;
    $scope.sendError = null;
    $scope.userSearchText = "";

    // Polling variables
    let pollingIntervalPromise = null;
    let isPolling = false;
    const POLLING_INTERVAL_MS = 3000; // Poll every 3 seconds

    // Initialize user list
    function initializeUsers() {
      if ($scope.currentUser && $scope.currentUser.username) {
        $scope.loggedInUsername = $scope.currentUser.username;
        console.log('[Init] Saved loggedInUsername:', $scope.loggedInUsername);

        // First get the current user's groups
        Restangular.one('user', $scope.loggedInUsername).get().then(function (userData) {
          if (userData && userData.groups && userData.groups.length > 0) {
            const userGroups = userData.groups;
            console.log('[Init] Current user belongs to groups:', userGroups);

            // Then get all users
            Restangular.one('user/list').get({ sort_column: 1, asc: true })
              .then(function (data) {
                if (data && data.users) {
                  // For each user, check if they share at least one group with the current user
                  const promises = data.users
                    .filter(u => u.username !== $scope.loggedInUsername)
                    .map(user => {
                      // Get user's groups
                      return Restangular.one('user', user.username).get()
                        .then(function (otherUserData) {
                          if (otherUserData && otherUserData.groups) {
                            // Check if user shares any group with current user
                            const sharedGroups = otherUserData.groups.filter(group =>
                              userGroups.includes(group));

                            if (sharedGroups.length > 0) {
                              if (user.unreadMessages === undefined) user.unreadMessages = 0;
                              if (!user.displayName) user.displayName = user.username;
                              user.sharedGroups = sharedGroups;
                              return user;
                            }
                          }
                          return null;
                        })
                        .catch(() => null);
                    });

                  // When all promises resolve, filter out nulls
                  Promise.all(promises).then(results => {
                    $scope.$apply(function () {
                      $scope.availableUsers = results.filter(Boolean);
                      console.log('[Init] Filtered users who share groups:', $scope.availableUsers);
                    });
                  });
                } else {
                  $scope.availableUsers = [];
                }
              }).catch(function (error) {
                console.error("[Init] Error loading users:", error);
                $scope.availableUsers = [];
              });
          } else {
            console.warn("[Init] Current user doesn't belong to any groups");
            $scope.availableUsers = [];
          }
        }).catch(function (error) {
          console.error("[Init] Error loading current user groups:", error);
          $scope.availableUsers = [];
        });
      } else {
        console.warn("[Init] Current user info not available.");
        $scope.loggedInUsername = null;
        $scope.availableUsers = [];
      }
    }

    // Load chat history from API
    function loadChatHistory(partnerUsername) {
      if (!$scope.loggedInUsername) { return; }
      if (!partnerUsername) { return; }

      console.log(`[History] Loading chat history between ${$scope.loggedInUsername} and ${partnerUsername}`);
      $scope.isLoadingHistory = true;
      $scope.messages[partnerUsername] = [];

      Restangular.one('chat/conversation', $scope.loggedInUsername)
        .one(partnerUsername).get({ sort: 'asc' })
        .then(function (response) {
          if (response && response.messages) {
            $scope.messages[partnerUsername] = response.messages.map(msg => {
              msg.timestamp = new Date(msg.timestamp);
              msg.content = msg.content || "";
              msg.text = msg.content;
              msg.sender = msg.senderUsername;
              return msg;
            });
            console.log(`[History] Loaded ${response.messages.length} messages for ${partnerUsername}`);
          } else {
            $scope.messages[partnerUsername] = [];
            console.log(`[History] No message history found for ${partnerUsername}`);
          }
          $timeout(scrollToBottom, 50);
        })
        .catch(function (error) {
          console.error(`[History] Error loading chat history with ${partnerUsername}:`, error);
          $scope.messages[partnerUsername] = [{
            id: 'error_load_' + Date.now(),
            sender: 'System',
            senderUsername: 'System',
            text: 'Failed to load history.',
            timestamp: new Date(),
            isError: true
          }];
        })
        .finally(function () {
          $scope.isLoadingHistory = false;
        });
    }

    // Poll for new messages
    function pollNewMessages() {
      if (isPolling || !$scope.selectedUserToChatWith || !$scope.loggedInUsername) {
        return;
      }

      isPolling = true;
      const partnerUsername = $scope.selectedUserToChatWith.username;

      Restangular.one('chat/conversation', $scope.loggedInUsername)
        .one(partnerUsername).get({ sort: 'asc' })
        .then(function (response) {
          if (response && response.messages &&
            $scope.selectedUserToChatWith &&
            $scope.selectedUserToChatWith.username === partnerUsername) {

            const fetchedMessages = response.messages.map(msg => {
              msg.timestamp = new Date(msg.timestamp);
              msg.content = msg.content || "";
              msg.text = msg.content;
              msg.sender = msg.senderUsername;
              return msg;
            });

            const currentMessages = $scope.messages[partnerUsername] || [];
            const currentMessageIds = new Set(currentMessages.map(m => m.id));

            // Filter out only new messages
            const newMessages = fetchedMessages.filter(msg =>
              msg.id &&
              !msg.id.startsWith('temp_') &&
              !msg.id.startsWith('error_') &&
              !currentMessageIds.has(msg.id)
            );

            if (newMessages.length > 0) {
              console.log(`[Polling] Found ${newMessages.length} new message(s)`);

              // Check if user is scrolled near the bottom
              const chatArea = document.getElementById('chatMessagesArea');
              let shouldScroll = true;

              if (chatArea) {
                const threshold = 50; // Pixels from bottom
                shouldScroll = chatArea.scrollHeight - chatArea.scrollTop <= chatArea.clientHeight + threshold;
              }

              // Append new messages
              $timeout(function () {
                Array.prototype.push.apply($scope.messages[partnerUsername], newMessages);

                // Scroll only if user was already near the bottom
                if (shouldScroll) {
                  scrollToBottom();
                }
              }, 0);
            }
          }
        })
        .catch(function (error) {
          console.error(`[Polling] Error fetching messages with ${partnerUsername}:`, error);
        })
        .finally(function () {
          isPolling = false;
        });
    }

    // Start polling for new messages
    function startPolling() {
      stopPolling();
      if ($scope.selectedUserToChatWith) {
        console.log(`[Polling] Starting polling for ${$scope.selectedUserToChatWith.username}`);
        pollNewMessages();
        pollingIntervalPromise = $interval(pollNewMessages, POLLING_INTERVAL_MS);
      }
    }

    // Stop polling for messages
    function stopPolling() {
      if (pollingIntervalPromise) {
        console.log("[Polling] Stopping polling");
        $interval.cancel(pollingIntervalPromise);
        pollingIntervalPromise = null;
        isPolling = false;
      }
    }

    // Select a user to chat with
    $scope.selectUserToChat = function (user) {
      if ($scope.selectedUserToChatWith && $scope.selectedUserToChatWith.username === user.username) {
        return;
      }

      console.log(`[Select] Selecting user: ${user.username}`);
      stopPolling();

      $scope.sendError = null;
      $scope.chatInput.text = '';
      $scope.selectedUserToChatWith = user;

      if (user) {
        user.unreadMessages = 0;
        loadChatHistory(user.username);
        startPolling();
      }
    };

    // Send a chat message
    $scope.sendMessage = function () {
      if (!$scope.chatInput.text.trim() || !$scope.selectedUserToChatWith || !$scope.loggedInUsername) {
        return;
      }

      $scope.sendError = null;
      const partnerUsername = $scope.selectedUserToChatWith.username;
      const senderUsername = $scope.loggedInUsername;
      const messageContent = $scope.chatInput.text;

      // Create optimistic message
      const optimisticMessage = {
        id: 'temp_' + Date.now() + Math.random(),
        sender: senderUsername,
        senderUsername: senderUsername,
        senderDisplayName: ($scope.currentUser ? $scope.currentUser.displayName : null) || senderUsername,
        recipientUsername: partnerUsername,
        content: messageContent,
        text: messageContent,
        timestamp: new Date(),
        isSending: true
      };

      if (!$scope.messages[partnerUsername]) {
        $scope.messages[partnerUsername] = [];
      }

      $scope.messages[partnerUsername].push(optimisticMessage);
      $timeout(scrollToBottom, 50);

      // Clear input
      $scope.chatInput.text = '';
      $timeout(angular.noop);

      // Send message via API
      const payload = {
        sender: senderUsername,
        recipient: partnerUsername,
        content: messageContent
      };

      console.log("[Send] Attempting to send API request with payload:", payload);

      Restangular.all('chat/messages').post(payload)
        .then(function (response) {
          console.log("[Send] Message sent successfully:", response);

          const messageIndex = $scope.messages[partnerUsername].findIndex(m => m.id === optimisticMessage.id);
          if (messageIndex > -1) {
            response.timestamp = new Date(response.timestamp);
            response.content = response.content || "";
            response.text = response.content;
            response.sender = response.senderUsername;
            $scope.messages[partnerUsername][messageIndex] = {
              ...response,
              senderDisplayName: optimisticMessage.senderDisplayName
            };
          } else {
            console.warn("[Send] Could not find optimistic message to update.");
          }
        })
        .catch(function (error) {
          console.error("[Send] Error sending message:", error);
          $scope.sendError = "Failed to send message.";

          const messageIndex = $scope.messages[partnerUsername].findIndex(m => m.id === optimisticMessage.id);
          if (messageIndex > -1) {
            $scope.messages[partnerUsername][messageIndex].isSending = false;
            $scope.messages[partnerUsername][messageIndex].isError = true;
            $scope.messages[partnerUsername][messageIndex].errorText = "Failed to send";
          } else {
            console.warn("[Send] Could not find optimistic message to mark failed.");
          }
        });
    };

    // Scroll chat to bottom
    function scrollToBottom() {
      var chatArea = document.getElementById('chatMessagesArea');
      if (chatArea) {
        $timeout(function () {
          chatArea.scrollTop = chatArea.scrollHeight;
        }, 0);
      }
    }

    // Watch for user info changes
    var unwatchUserInfo = $rootScope.$watch('userInfo', function (newUserInfo, oldUserInfo) {
      console.log("[Watcher] userInfo changed:", newUserInfo);

      if (newUserInfo && newUserInfo.username &&
        (!oldUserInfo || newUserInfo.username !== oldUserInfo.username)) {

        $scope.currentUser = newUserInfo;
        $scope.loggedInUsername = $scope.currentUser.username;
        console.log('[Watcher] Updated loggedInUsername:', $scope.loggedInUsername);

        stopPolling();
        $scope.selectedUserToChatWith = null;
        $scope.messages = {};
        initializeUsers();
      }
      else if (!newUserInfo && oldUserInfo) {
        console.log("[Watcher] User logged out.");
        stopPolling();
        $scope.currentUser = null;
        $scope.loggedInUsername = null;
        $scope.availableUsers = [];
        $scope.selectedUserToChatWith = null;
        $scope.messages = {};
        $scope.sendError = null;
        $scope.chatInput.text = '';
      }
      else if (newUserInfo && !newUserInfo.username) {
        console.warn("[Watcher] UserInfo exists but lacks username.");
        stopPolling();
        $scope.loggedInUsername = null;
        $scope.selectedUserToChatWith = null;
      }
    });

    // Initial check
    if ($scope.currentUser && $scope.currentUser.username) {
      console.log("[Init Check] UserInfo already available on load.");
      initializeUsers();
    }

    // Cleanup when controller is destroyed
    $scope.$on('$destroy', function () {
      console.log("[Destroy] Cleaning up UserGroupDefaultChatCtrl.");
      unwatchUserInfo();
      stopPolling();
    });
  });