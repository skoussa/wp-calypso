/**
 * Internal dependencies
 */
import { combineReducers, withStorageKey } from 'calypso/state/utils';
import partner from 'calypso/state/partner-portal/partner/reducer';
import licenses from 'calypso/state/partner-portal/licenses/reducer';

const combinedReducer = combineReducers( {
	partner,
	licenses,
} );

export default withStorageKey( 'partnerPortal', combinedReducer );
