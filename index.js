import { NativeModules, Platform, Linking } from 'react-native'

const NotificationSettings = {
  appSettingsOpen: () => {
    if (Platform.OS === 'ios') {
      Linking.openURL('app-settings:')
    } else {
      NativeModules.OpenNotification.open()
    }
  },
  channelSettingsOpen: () => {
    NativeModules.OpenNotification.openChannelSettings()
  }
}


module.exports = NotificationSettings
