/**
 * External dependencies
 */
import find from 'lodash/find';

/**
 * Internal dependencies
 */
import { License, PartnerPortalStore } from 'calypso/state/partner-portal';
// Required for modular state.
import 'calypso/state/partner-portal/init';

export function hasFetchedLicenses( state: PartnerPortalStore ): boolean {
	return state.partnerPortal.licenses.hasFetched;
}

export function isFetchingLicenses( state: PartnerPortalStore ): boolean {
	return state.partnerPortal.licenses.isFetching;
}

export function getLicenses( state: PartnerPortalStore ): License[] {
	return state.partnerPortal.licenses.all;
}

export function getLicensesError( state: PartnerPortalStore ): string {
	return state.partnerPortal.licenses.error;
}
