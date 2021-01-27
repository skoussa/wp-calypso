/**
 * Returns whether user is using a WordPress mobile app.
 *
 * @returns {boolean} Whether the user agent matches the ones used on the WordPress mobile apps.
 */
export function isWpMobileApp() {
	if ( typeof navigator === 'undefined' || navigator === null ) {
		return false;
	}
	return navigator && /wp-(android|iphone)/.test( navigator.userAgent );
}
